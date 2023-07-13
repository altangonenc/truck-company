import matplotlib.pyplot as plt

# pip install matplotlib
# py countryGraph.py


# Latitudes and longitudes 
latitudes = [42.546245, 23.424076, 33.93911, 41.153332, 40.069099, -38.416097, 47.516231, 40.143105, 43.915886,
             50.503887, 42.733883, 25.930414, -14.235004, 56.130366, 46.818188, -35.675147, 35.86166, 4.570868,
             49.817492, 51.165691, 56.26392, 28.033886, -1.831239, 26.820553, 40.463667, 61.92411, 46.603354,
             42.315407, 39.074208, 22.396428, 45.1, 47.162494, -0.789275, 53.41291, 31.046051, 20.593684,
             64.963051, 41.87194, 36.204824, -0.023559, 41.20438, 35.907757, 29.31166, 48.019573, 33.854721,
             55.169438, 56.879635, 31.791702, 42.708678, -18.766947, 41.608635, 46.862496, 23.634501, 4.210484,
             -18.665695, 9.081999, 52.132633, 60.472024, -9.189967, 30.375321, 51.919438, 39.399872, -23.442503,
             25.354826, 45.943161, 44.016521, 61.52401, 23.885942, 60.128161, 1.352083, 46.151241, 48.669026,
             14.497401, 34.802075, -26.522503, 15.870032, 38.969719, 38.963745, 48.379433, 1.373333, 37.09024,
             -32.522779, 41.377491, 6.42375, 14.058324, -30.559482]

longitudes = [1.601554, 53.847818, 67.709953, 20.168331, 45.038189, -63.616672, 14.550072, 47.576927, 17.679076,
              4.469936, 25.48583, 50.637772, -51.92528, -106.346771, 8.227512, -71.542969, 104.195397, -74.297333,
              15.472962, 10.451526, 9.501785, 1.659626, -78.183406, 30.802498, -3.74922, 25.748151, 1.888334,
              43.356892, 21.824312, 114.109497, 15.2, 19.503304, 113.921327, -8.24389, 34.851612, 78.96288,
              -19.020835, 12.56738, 138.252924, 37.906193, 74.766098, 127.766922, 47.481766, 66.923684, 35.862285,
              23.881275, 24.603189, -7.09262, 19.37439, 46.869107, 21.745275, 103.846656, -102.552784, 101.975766,
              35.529562, 8.675277, 5.291266, 8.468946, -75.015152, 69.345116, 19.145136, -8.224454, -58.443832,
              51.183884, 24.96676, 21.005859, 105.318756, 45.079162, 18.643501, 103.819836, 14.995463, 19.699024,
              -14.452362, 38.996815, 31.465866, 100.992541, 59.556278, 35.243322, 31.16558, 32.290275, -95.712891,
              -55.765835, 64.585262, -66.58973, 108.277199, 22.937506]

names = ["Andorra", "United Arab Emirates", "Afghanistan", "Albania", "Armenia", "Argentina", "Austria",
         "Azerbaijan", "Bosnia and Herzegovina", "Belgium", "Bulgaria", "Bahrain", "Brazil", "Canada", "Switzerland",
         "Chile", "China", "Colombia", "Czech Republic", "Germany", "Denmark", "Algeria", "Ecuador", "Egypt", "Spain",
         "Finland", "France", "Georgia", "Greece", "Hong Kong", "Croatia", "Hungary", "Indonesia", "Ireland", "Israel",
         "India", "Iceland", "Italy", "Japan", "Kenya", "Kyrgyzstan", "South Korea", "Kuwait", "Kazakhstan", "Lebanon",
         "Lithuania", "Latvia", "Morocco", "Montenegro", "Madagascar", "Macedonia", "Mongolia", "Mexico", "Malaysia",
         "Mozambique", "Nigeria", "Netherlands", "Norway", "Peru", "Pakistan", "Poland", "Portugal", "Paraguay", "Qatar",
         "Romania", "Serbia", "Russia", "Saudi Arabia", "Sweden", "Singapore", "Slovenia", "Slovakia", "Senegal", "Syria",
         "Swaziland", "Thailand", "Turkmenistan", "Turkey", "Ukraine", "Uganda", "United States", "Uruguay", "Uzbekistan",
         "Venezuela", "Vietnam", "South Africa"]

# Create Graphic
plt.figure(figsize=(10, 6))
plt.scatter(longitudes, latitudes, marker='o', color='red')

for i, name in enumerate(names):
    plt.text(longitudes[i], latitudes[i], name, fontsize=8, ha='left')

# axisis and title
plt.xlabel('Longitude')
plt.ylabel('Latitude')
plt.title('Freight Terminals')

plt.show()

