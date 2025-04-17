#!/bin/bash

JWT_FILE="/tmp/jwt_token"
API_TOKEN_FILE="/tmp/api_token"

function register() {
  echo "=== Register ==="
  read -p "Email: " email
  read -s -p "Password: " password
  echo
  read -p "From Name (used as sender name): " fromName

  curl -X POST http://localhost:8080/register \
    -H "Content-Type: application/json" \
    -d "{
      \"email\": \"$email\",
      \"password\": \"$password\",
      \"fromName\": \"$fromName\"
    }"
}

function login() {
  echo "=== Login ==="
  read -p "Email: " email
  read -s -p "Password: " password
  echo
  response=$(curl -s -X POST http://localhost:8080/login \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$email\", \"password\":\"$password\"}")

  jwt=$(echo "$response" | grep -oP '(?<="token":")[^"]+')
  if [[ -n "$jwt" ]]; then
    echo "$jwt" > "$JWT_FILE"
    echo "✅ Logged in. JWT stored."
    fetch_api_token
  else
    echo "❌ Login failed. Response: $response"
  fi
}

function fetch_api_token() {
  echo "🔍 Fetching API token from /profile..."
  jwt=$(cat "$JWT_FILE")
  response=$(curl -s -X GET http://localhost:8080/profile \
    -H "Authorization: Bearer $jwt")

  apiToken=$(echo "$response" | grep -oP '(?<="apiToken":")[^"]+')
  if [[ -n "$apiToken" ]]; then
    echo "$apiToken" > "$API_TOKEN_FILE"
    echo "✅ Api-Token retrieved and saved."
  else
    echo "❌ Failed to fetch Api-Token. Response: $response"
  fi
}

function check_profile() {
  echo "=== Check Profile ==="
  if [[ ! -f "$JWT_FILE" ]]; then
    echo "❌ Please login first."
    return
  fi
  jwt=$(cat "$JWT_FILE")
  curl -X GET http://localhost:8080/profile \
    -H "Authorization: Bearer $jwt"
}

function send_email() {
  echo "=== Send Email ==="
  if [[ ! -f "$API_TOKEN_FILE" ]]; then
    echo "❌ Please login to fetch your Api-Token first."
    return
  fi
  read -p "To: " to
  read -p "Subject: " subject
  read -p "Body: " body
  apiToken=$(cat "$API_TOKEN_FILE")
  curl -X POST http://localhost:8080/send \
    -H "Content-Type: application/json" \
    -H "Api-Token: $apiToken" \
    -d "{\"recipient\":\"$to\", \"subject\":\"$subject\", \"body\":\"$body\"}"
}

function show_menu() {
  echo ""
  echo "====== Email API CLI ======"
  echo "1. Register"
  echo "2. Login"
  echo "3. Check Profile"
  echo "4. Send Email"
  echo "5. Exit"
  echo "==========================="
}

while true; do
  show_menu
  read -p "Choose an option [1-5]: " choice
  case $choice in
    1) register ;;
    2) login ;;
    3) check_profile ;;
    4) send_email ;;
    5) echo "👋 Bye!"; exit 0 ;;
    *) echo "❌ Invalid option." ;;
  esac
done
