import smtplib
import sys

def check_brevo_credentials():
    # ==============================
    # ENTER YOUR CREDENTIALS HERE
    # ==============================
    smtp_host = "smtp-relay.brevo.com"
    smtp_port = 587
    
    # This must be your email address (e.g., user@gmail.com)
    username = "9c0860001@smtp-brevo.com" 
    
    # This must be the long generated key (e.g., xsmtpsib-...)
    password = "xsmtpsib-60511cfbb7fec3c58e439840b79bf76bcf9567d2d84553aa14683429db1da6ef-UMmujCj4pM8Ez1Ra"
    # ==============================

    try:
        print(f"1. Connecting to {smtp_host}:{smtp_port}...")
        server = smtplib.SMTP(smtp_host, smtp_port)
        
        print("2. Starting TLS...")
        server.starttls()
        
        print(f"3. Attempting to log in as {username}...")
        server.login(username, password)
        
        print("\nSUCCESS! Your credentials are correct.")
        print("The issue is likely inside your Spring Boot configuration (whitespaces, property loading, etc).")
        
        server.quit()
        
    except smtplib.SMTPAuthenticationError as e:
        print("\nAUTHENTICATION FAILED (535).")
        print("Response:", e)
        print("\nFix:")
        print("1. Check if the Username is exactly your Brevo login email.")
        print("2. The Password is WRONG. Generate a new SMTP Key in Brevo -> SMTP & API -> SMTP.")
        
    except Exception as e:
        print(f"\nCONNECTION ERROR: {e}")

if __name__ == "__main__":
    check_brevo_credentials()