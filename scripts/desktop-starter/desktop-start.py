import os
import time

def run_spring_application():
    try:
        os.system(r'java -jar C:\Users\altan\Desktop\truck-company-all\truck-company\target\truck-company-0.0.1-SNAPSHOT.jar')
    except Exception as e:
        print(f"Java uygulamasında bir hata oluştu: {e}")

if __name__ == "__main__":
    run_spring_application()

    time.sleep(10)
