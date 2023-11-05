import tkinter as tk
import os
import time
from tkinter import messagebox

def run_application():
    os.system(r'C:\Users\altan\Desktop\truck-company-all\truck-company\scripts\desktop-starter\dist\desktop-start.exe')

def update_jar():
    try:
        #mvn clean
        os.system('mvn clean')
        #mvn install 
        os.system('mvn install')

        messagebox.showinfo("Succesfull", "mvn clean and mvn install operations executed successfully.")
        
    except Exception as e: 
        print (f"Exception occured in maven operations. Exception is: {e}")
    

root = tk.Tk()

start_button = tk.Button(root, text="Start Application", command=run_application)
start_button.pack(pady=10)

update_button = tk.Button(root, text="Update jar", command=update_jar)
update_button.pack(pady=10)

root.mainloop()
