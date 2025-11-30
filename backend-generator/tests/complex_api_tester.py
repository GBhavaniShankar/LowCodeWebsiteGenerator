import requests
import json
import random
import string
import sys

# ==========================================
# CONFIGURATION
# ==========================================
BASE_URL = "http://localhost:8080"
ADMIN_EMAIL = "admin@agile.com"
ADMIN_PASS = "adminpass"

# Colors for terminal output
class Colors:
    HEADER = '\033[95m'
    OKGREEN = '\033[92m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def print_status(step, message, success):
    symbol = "✅ PASS" if success else "❌ FAIL"
    color = Colors.OKGREEN if success else Colors.FAIL
    print(f"{color}[{step}] {symbol}: {message}{Colors.ENDC}")
    if not success:
        sys.exit(1)

def generate_random_email():
    random_str = ''.join(random.choices(string.ascii_lowercase + string.digits, k=6))
    return f"dev_{random_str}@agile.com"

class ComplexTester:
    def __init__(self):
        self.session = requests.Session()
        self.admin_token = None
        self.user_token = None
        self.team_id = None
        self.sprint_id = None

    def run(self):
        print(f"{Colors.HEADER}Starting Complex Hierarchy Tests...{Colors.ENDC}\n")
        
        self.step_1_admin_login()
        self.step_2_create_team()
        self.step_3_create_sprint()
        self.step_4_user_workflow()
        
        print(f"\n{Colors.OKGREEN}{Colors.BOLD}🎉 HIERARCHY TEST COMPLETED SUCCESSFULLY!{Colors.ENDC}")

    # ----------------------------------------------------------------
    # ADMIN ACTIONS
    # ----------------------------------------------------------------
    
    def step_1_admin_login(self):
        res = requests.post(f"{BASE_URL}/api/auth/login", json={"email": ADMIN_EMAIL, "password": ADMIN_PASS})
        if res.status_code == 200:
            self.admin_token = res.json()["accessToken"]
            print_status("1. Admin Login", f"Success ({ADMIN_EMAIL})", True)
        else:
            print_status("1. Admin Login", f"Failed: {res.text}", False)

    def step_2_create_team(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        payload = {"name": "DevOps Team", "description": "Infrastructure handlers"}
        
        res = requests.post(f"{BASE_URL}/api/Team", json=payload, headers=headers)
        if res.status_code == 200:
            self.team_id = res.json()["id"]
            print_status("2. Create Team", f"Created Team ID: {self.team_id}", True)
        else:
            print_status("2. Create Team", f"Failed: {res.text}", False)

    def step_3_create_sprint(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        # REF LOGIC: We pass the object with just the ID
        payload = {
            "name": "Sprint 42",
            "goal": "Migrate Database",
            "team": {"id": self.team_id} 
        }
        
        res = requests.post(f"{BASE_URL}/api/Sprint", json=payload, headers=headers)
        if res.status_code == 200:
            self.sprint_id = res.json()["id"]
            print_status("3. Create Sprint", f"Created Sprint ID: {self.sprint_id} (Linked to Team {self.team_id})", True)
        else:
            print_status("3. Create Sprint", f"Failed: {res.text}", False)

    # ----------------------------------------------------------------
    # USER ACTIONS
    # ----------------------------------------------------------------

    def step_4_user_workflow(self):
        email = generate_random_email()
        password = "password123"
        
        # A. Register
        requests.post(f"{BASE_URL}/api/auth/register", json={"email": email, "password": password})
        
        # B. Login
        res = requests.post(f"{BASE_URL}/api/auth/login", json={"email": email, "password": password})
        self.user_token = res.json()["accessToken"]
        print_status("4. User Register", f"User {email} logged in", True)
        
        user_headers = {"Authorization": f"Bearer {self.user_token}"}

        # C. Security Check: Try to create a Team (Should Fail)
        fail_payload = {"name": "Hacker Team", "description": "Should not exist"}
        res = requests.post(f"{BASE_URL}/api/Team", json=fail_payload, headers=user_headers)
        if res.status_code == 403:
            print_status("5. Security Check", "User blocked from creating Team (403)", True)
        else:
            print_status("5. Security Check", f"SECURITY FAIL! User created Team (Code {res.status_code})", False)

        # D. Get Sprints (To find ID)
        res = requests.get(f"{BASE_URL}/api/Sprint", headers=user_headers)
        if res.status_code == 200 and len(res.json()) > 0:
            found_sprint_id = res.json()[0]["id"]
            print_status("6. View Sprints", f"User found Sprint ID: {found_sprint_id}", True)
        else:
            print_status("6. View Sprints", "Failed to retrieve Sprints", False)

        # E. Create Ticket (Linked to Sprint)
        ticket_payload = {
            "title": "Upgrade PostgreSQL",
            "status": "TODO",
            "sprint": {"id": self.sprint_id}
        }
        res = requests.post(f"{BASE_URL}/api/Ticket", json=ticket_payload, headers=user_headers)
        if res.status_code == 200:
            print_status("7. Create Ticket", "User successfully created Ticket linked to Sprint", True)
        else:
            print_status("7. Create Ticket", f"Failed: {res.text}", False)

        # F. View Own Tickets
        res = requests.get(f"{BASE_URL}/api/Ticket/my", headers=user_headers)
        if res.status_code == 200 and len(res.json()) == 1:
            print_status("8. View My Tickets", "User sees exactly 1 ticket", True)
        else:
            print_status("8. View My Tickets", f"Failed or wrong count: {res.text}", False)

if __name__ == "__main__":
    ComplexTester().run()