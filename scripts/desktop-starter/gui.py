import tkinter as tk
import os

def run_application():
    os.system(r'C:\Users\altan\Desktop\truck-company-all\truck-company\scripts\desktop-starter\dist\desktop-start.exe')

root = tk.Tk()
button = tk.Button(root, text="Start Application", command=run_application)
button.pack()
root.mainloop()
