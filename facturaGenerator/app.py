from flask import Flask, request, send_file, jsonify
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer, Image
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_RIGHT, TA_LEFT
from reportlab.pdfgen import canvas
from datetime import datetime
from num2words import num2words
import os
import io
import logging
import traceback
import boto3
from botocore.client import Config
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)


R2_ACCESS_KEY_ID = os.getenv('R2_ACCESS_KEY_ID')
R2_SECRET_ACCESS_KEY = os.getenv('R2_SECRET_ACCESS_KEY')
R2_ENDPOINT_URL = os.getenv('R2_ENDPOINT_URL')
R2_BUCKET_NAME = os.getenv('R2_BUCKET_NAME', 'facturas-corocora')
R2_PUBLIC_URL = os.getenv('R2_PUBLIC_URL')

s3_client = None
if R2_ACCESS_KEY_ID and R2_SECRET_ACCESS_KEY and R2_ENDPOINT_URL:
    s3_client = boto3.client(
        's3',
        endpoint_url=R2_ENDPOINT_URL,
        aws_access_key_id=R2_ACCESS_KEY_ID,
        aws_secret_access_key=R2_SECRET_ACCESS_KEY,
        config=Config(signature_version='s3v4'),
        region_name='auto'
    )

else:
    logger.warning("Credenciales R2 no configuradas - funcionando en modo local")

def subir_pdf_a_r2(pdf_buffer, filename):

    try:
        if not s3_client:
            raise Exception("Cliente R2 no inicializado. Verifica las credenciales.")


        pdf_buffer.seek(0)


        s3_client.put_object(
            Bucket=R2_BUCKET_NAME,
            Key=filename,
            Body=pdf_buffer.getvalue(),
            ContentType='application/pdf',
            ContentDisposition=f'inline; filename="{filename}"',
        )


        if R2_PUBLIC_URL:
            public_url = f"{R2_PUBLIC_URL}/{filename}"
        else:
            public_url = s3_client.generate_presigned_url(
                'get_object',
                Params={
                    'Bucket': R2_BUCKET_NAME,
                    'Key': filename
                },
                ExpiresIn=604800
            )

        return public_url

    except Exception as e:
        logger.error(f"Error subiendo PDF a R2: {str(e)}")
        raise

@app.route('/generate-factura', methods=['POST'])
def generate_factura():
    try:

        raw_data = request.get_data(as_text=True)

        if not request.is_json:
            return jsonify({'error': 'Content-Type debe ser application/json'}), 400

        try:
            import json
            import re

            cleaned_data = raw_data.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ')
            cleaned_data = re.sub(r'\s+', ' ', cleaned_data)
            data = json.loads(cleaned_data)
        except json.JSONDecodeError as je:
            return jsonify({'error': f'JSON invalido: {str(je)}', 'raw_data': raw_data}), 400

        nombre_cliente = data.get('nombrecliente', '')
        documento_cliente = data.get('documentocliente', '')
        productos_str = data.get('productos', '')

        if not nombre_cliente or not documento_cliente or not productos_str:
            return jsonify({'error': 'Faltan parámetros requeridos'}), 400

        productos = parsear_productos(productos_str)

        if len(productos) == 0:
            return jsonify({'error': 'No se pudieron parsear productos válidos'}), 400


        pdf_buffer = generar_pdf_factura(nombre_cliente, documento_cliente, productos)
        filename = f'factura_{documento_cliente}_{datetime.now().strftime("%Y%m%d_%H%M%S")}.pdf'
        pdf_url = subir_pdf_a_r2(pdf_buffer, filename)
        return jsonify({
            'success': True,
            'message': 'Factura generada exitosamente',
            'pdfUrl': pdf_url,

        }), 200

    except Exception as e:
        return jsonify({'error': str(e), 'traceback': traceback.format_exc()}), 500


def parsear_productos(productos_str):

    try:

        productos = []
        items = productos_str.strip().split(';')
        for item in items:
            item = item.strip()
            if not item:
                continue

            valores = item.split(',')

            if len(valores) >= 5:
                producto = {
                    'id': valores[0].strip(),
                    'nombre': valores[1].strip(),
                    'precio_unitario': float(valores[2].strip()),
                    'cantidad': int(valores[3].strip()),
                    'precio_parcial': float(valores[4].strip())
                }
                productos.append(producto)
            

        return productos
    except Exception as e:
        raise


def generar_pdf_factura(nombre_cliente, documento_cliente, productos):
    try:

        buffer = io.BytesIO()

        doc = SimpleDocTemplate(buffer, pagesize=letter,
                               rightMargin=50, leftMargin=50,
                               topMargin=50, bottomMargin=50)
        elements = []

        styles = getSampleStyleSheet()
        header_style = ParagraphStyle(
            'HeaderStyle',
            parent=styles['Normal'],
            fontSize=10,
            textColor=colors.HexColor('#2C3E50'),
            alignment=TA_RIGHT,
            spaceAfter=5
        )

        title_style = ParagraphStyle(
            'CustomTitle',
            parent=styles['Heading1'],
            fontSize=28,
            textColor=colors.HexColor('#2C3E50'),
            spaceAfter=20,
            alignment=TA_CENTER,
            fontName='Helvetica-Bold'
        )

        subtitle_style = ParagraphStyle(
            'CustomSubtitle',
            parent=styles['Normal'],
            fontSize=11,
            textColor=colors.HexColor('#34495E'),
            spaceAfter=8,
            alignment=TA_LEFT
        )

        total_style = ParagraphStyle(
            'TotalStyle',
            parent=styles['Normal'],
            fontSize=12,
            textColor=colors.HexColor('#2C3E50'),
            spaceAfter=5,
            alignment=TA_LEFT,
            fontName='Helvetica-Bold'
        )

        logo_path = os.path.join(os.path.dirname(__file__), 'logofactura.png')
        if os.path.exists(logo_path):
            logo_img = Image(logo_path, width=1.5*inch, height=0.75*inch)
            header_data = [[logo_img]]
            header_table = Table(header_data, colWidths=[6.5*inch])
            header_table.setStyle(TableStyle([
                ('ALIGN', (0, 0), (-1, -1), 'RIGHT'),
                ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ]))
            elements.append(header_table)

        empresa_info = Paragraph("<b>Corocora Mayorista</b>", header_style)
        elements.append(empresa_info)

        nit_info = Paragraph("NIT: 0000000000", header_style)
        elements.append(nit_info)

        ubicacion_info = Paragraph("Villavicencio, Meta", header_style)
        elements.append(ubicacion_info)

        elements.append(Spacer(1, 0.4*inch))

        titulo = Paragraph("FACTURA", title_style)
        elements.append(titulo)
        elements.append(Spacer(1, 0.3*inch))

        fecha = Paragraph(f"<b>Fecha de Emisión:</b> {datetime.now().strftime('%d/%m/%Y %H:%M')}", subtitle_style)
        elements.append(fecha)
        elements.append(Spacer(1, 0.2*inch))
        info_cliente = Paragraph(f"<b>Nombre:</b> {nombre_cliente}", subtitle_style)
        elements.append(info_cliente)

        doc_cliente = Paragraph(f"<b>Documento:</b> {documento_cliente}", subtitle_style)
        elements.append(doc_cliente)
        elements.append(Spacer(1, 0.3*inch))


        pedido_titulo = Paragraph("<b>Datos del Pedido:</b>", subtitle_style)
        elements.append(pedido_titulo)
        elements.append(Spacer(1, 0.1*inch))

        tabla_data = [['ID', 'Nombre Producto', 'Precio Unitario', 'Cantidad', 'Precio Parcial']]

        precio_total = 0

        for producto in productos:
            tabla_data.append([
                producto['id'],
                producto['nombre'],
                f"${producto['precio_unitario']:,.2f}",
                str(producto['cantidad']),
                f"${producto['precio_parcial']:,.2f}"
            ])
            precio_total += producto['precio_parcial']

        tabla = Table(tabla_data, colWidths=[0.8*inch, 2.5*inch, 1.3*inch, 1*inch, 1.3*inch])

        tabla.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#3498DB')),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, 0), 'CENTER'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 11),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('TOPPADDING', (0, 0), (-1, 0), 12),

            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 1), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 10),
            ('GRID', (0, 0), (-1, -1), 1, colors.black),
            ('TOPPADDING', (0, 1), (-1, -1), 8),
            ('BOTTOMPADDING', (0, 1), (-1, -1), 8),
        ]))

        elements.append(tabla)
        elements.append(Spacer(1, 0.3*inch))

        total_precio = Paragraph(f"<b>TOTAL: ${precio_total:,.2f}</b>", total_style)
        elements.append(total_precio)

        try:
            total_en_palabras = num2words(int(precio_total), lang='es').upper()
            total_centavos = int((precio_total - int(precio_total)) * 100)

            if total_centavos > 0:
                centavos_texto = num2words(total_centavos, lang='es')
                total_texto = f"{total_en_palabras} CON {centavos_texto.upper()} PESOS"
            else:
                total_texto = f"{total_en_palabras} PESOS"

        except:
            total_texto = f"{precio_total:,.2f} PESOS"

        total_letras = Paragraph(f"<i>{total_texto}</i>",
                                ParagraphStyle('TotalLetras', parent=styles['Normal'],
                                             fontSize=10, alignment=TA_LEFT,
                                             textColor=colors.HexColor('#555555')))
        elements.append(total_letras)
        elements.append(Spacer(1, 0.5*inch))

        footer = Paragraph(
            "<i>Gracias por su compra</i>",
            ParagraphStyle('Footer', parent=styles['Normal'], alignment=TA_CENTER,
                          fontSize=10, textColor=colors.grey)
        )
        elements.append(footer)
        doc.build(elements)

        return buffer
    except Exception as e:
        logger.error(f"Error en generar_pdf_factura: {str(e)}")
        raise


@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok', 'service': 'factura-generator'}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
