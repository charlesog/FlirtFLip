import firebase_admin
from firebase_admin import credentials, firestore
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# 1. Configuración de Firebase
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# 2. Configuración de tu correo (El que enviará los mensajes)
SMTP_SERVER = "smtp.gmail.com"
SMTP_PORT = 587
SENDER_EMAIL = "procesoscharles@gmail.com"
SENDER_PASSWORD = "norn ygws rgab tnbx" # No es tu clave normal, es la de 16 letras de Google

def enviar_correo(destinatario, nombre_usuario):
    try:
        msg = MIMEMultipart()
        msg['From'] = SENDER_EMAIL
        msg['To'] = destinatario
        msg['Subject'] = "¡Bienvenido a FlirtFlip! ❤️"

        cuerpo = f"Hola {nombre_usuario},\n\n¡Gracias por unirte a FlirtFlip! Tu perfil ya está casi listo. ¡Esperamos que encuentres lo que buscas!"
        msg.attach(MIMEText(cuerpo, 'plain'))

        server = smtplib.SMTP(SMTP_SERVER, SMTP_PORT)
        server.starttls()
        server.login(SENDER_EMAIL, SENDER_PASSWORD)
        server.sendmail(SENDER_EMAIL, destinatario, msg.as_string())
        server.quit()
        print(f"✅ Correo enviado con éxito a {destinatario}")
    except Exception as e:
        print(f"❌ Error al enviar correo: {e}")

# 3. "Vigilante" de Firestore (Ajustado a tus capturas)
def on_snapshot(col_snapshot, changes, read_time):
    for change in changes:
        # Usamos ADDED o MODIFIED para asegurar que pesque el nombre 
        # si se rellena justo después de crear la cuenta
        if change.type.name == 'ADDED' or change.type.name == 'MODIFIED':
            datos = change.document.to_dict()
            
            # Extraemos los campos exactos de tu imagen
            email_usuario = datos.get('email')
            nombre_real = datos.get('name') # 'name' es el campo de tu captura
            es_completo = datos.get('profileComplete', False)
            
            # Solo enviamos el correo si tenemos el email Y el nombre ya existe
            if email_usuario and nombre_real:
                print(f"🔔 Procesando usuario: {nombre_real} ({email_usuario})")
                enviar_correo(email_usuario, nombre_real)

# Escuchar cambios en la colección "users"
col_query = db.collection(u'users')
query_watch = col_query.on_snapshot(on_snapshot)

print("🚀 Vigilante de FlirtFlip activado... Esperando nuevos registros.")
while True:
    pass # Mantiene el script corriendo