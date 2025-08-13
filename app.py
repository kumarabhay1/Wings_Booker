from flask import Flask, request, jsonify
import requests
import json
import os
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # ✅ Allow cross-origin requests (important for Android)

# API config for flight data (AviationStack)
AVIATIONSTACK_API_KEY = 'YOUR_API_KEY'
BASE_URL = 'http://api.aviationstack.com/v1/flights'

BOOKINGS_FILE = 'bookings.json'  # 📁 File to store passenger bookings


@app.route('/')
def home():
    return '✅ Welcome to the Air Ticket API! Use /flights?from=IATA&to=IATA to search.'


# ✅ FLIGHT SEARCH ENDPOINT
@app.route('/flights', methods=['GET'])
def get_flights():
    dep_iata = request.args.get('from')
    arr_iata = request.args.get('to')

    # Check for required parameters
    if not dep_iata or not arr_iata:
        return jsonify({'error': '❌ Missing from or to parameter'}), 400

    params = {
        'access_key': AVIATIONSTACK_API_KEY,
        'dep_iata': dep_iata,
        'arr_iata': arr_iata
    }

    try:
        # Send request to AviationStack API
        response = requests.get(BASE_URL, params=params)
        data = response.json()

        if "data" not in data:
            return jsonify({'error': 'No flight data found'}), 404

        # Format the results
        result = []
        for flight in data["data"]:
            result.append({
                'airline': flight.get('airline', {}).get('name', 'Unknown'),
                'flight_number': flight.get('flight', {}).get('iata', 'N/A'),
                'departure_time': flight.get('departure', {}).get('scheduled', 'N/A'),
                'arrival_time': flight.get('arrival', {}).get('scheduled', 'N/A'),
                'departure_airport': flight.get('departure', {}).get('airport', ''),
                'arrival_airport': flight.get('arrival', {}).get('airport', ''),
                'duration': '2h 30m',  # 🕒 Static or calculated if needed
                'price': '₹' + str(3500 + len(flight.get("flight", {}).get("iata", "")) * 100)  # 💰 Dynamic price logic
            })

        return jsonify(result)

    except Exception as e:
        return jsonify({'error': 'Internal server error', 'message': str(e)}), 500


# ✅ FLIGHT BOOKING ENDPOINT
@app.route('/book', methods=['POST'])
def book_flight():
    try:
        data = request.get_json()

        # Extract passenger info
        full_name = data.get('fullName')
        email = data.get('email')
        phone = data.get('phone')
        age = data.get('age')
        gender = data.get('gender')
        nationality = data.get('nationality')

        # 🛂 Basic validation
        if not full_name or not email or not phone:
            return jsonify({'error': 'Missing required passenger details'}), 400

        # ✅ Create the bookings.json file if not exists
        if not os.path.exists(BOOKINGS_FILE):
            with open(BOOKINGS_FILE, 'w') as f:
                json.dump([], f)

        # 📥 Read existing bookings
        with open(BOOKINGS_FILE, 'r') as f:
            bookings = json.load(f)

        # 🆕 New booking entry
        new_booking = {
            'fullName': full_name,
            'email': email,
            'phone': phone,
            'age': age,
            'gender': gender,
            'nationality': nationality
        }

        bookings.append(new_booking)

        # 💾 Write updated data back to file
        with open(BOOKINGS_FILE, 'w') as f:
            json.dump(bookings, f, indent=4)

        print("✅ New Booking Saved:", new_booking)

        return jsonify({'message': '✅ Booking confirmed successfully'}), 200

    except Exception as e:
        return jsonify({'error': '❌ Failed to process booking', 'message': str(e)}), 500

@app.route('/update_profile', methods=['POST'])
def update_profile():
    try:
        data = request.get_json()

        required_fields = ['email', 'name', 'phone', 'age', 'gender', 'nationality']
        for field in required_fields:
            if field not in data or not data[field]:
                return jsonify({'error': f'Missing or empty field: {field}'}), 400

        # Optional: Save or update this profile info in a file/database
        with open('profile_data.json', 'w') as f:
            json.dump(data, f)

        return jsonify({'message': 'Profile updated successfully'})
    except Exception as e:
        return jsonify({'error': 'Server error', 'message': str(e)}), 500

    data = request.get_json()
    email = data.get("email")

    if not email:
        return jsonify({"error": "Email is required"}), 400

    # simulate success
    return jsonify({"message": "Profile updated successfully"}), 200
if __name__ == '__main__':
    # 📡 Host: 0.0.0.0 to make it accessible from Android emulator
    app.run(host="0.0.0.0", port=5000, debug=True)

