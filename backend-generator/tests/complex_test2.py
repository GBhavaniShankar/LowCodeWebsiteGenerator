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
    WARNING = '\033[93m'

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
        self.user1_token = None
        self.user2_token = None
        self.team_id = None
        self.sprint_id = None

    def run(self):
        print(f"{Colors.HEADER}Starting Complex Hierarchy & Security Tests...{Colors.ENDC}\n")
        
        # Phase 1: Infrastructure (Admin)
        self.step_01_admin_login()
        self.step_02_create_team()
        self.step_03_create_sprint()
        
        # Phase 2: First User Workflow
        self.step_04_user1_register_login()
        self.step_05_user1_create_ticket()
        
        # Phase 3: Multi-User Isolation (The Complex Part)
        self.step_06_user2_register_login()
        self.step_07_data_isolation_check()
        self.step_08_user2_create_ticket()
        
        # Phase 4: Admin Oversight & User Restrictions
        self.step_09_admin_view_all()
        self.step_10_user_security_boundary()
        
        print(f"\n{Colors.OKGREEN}{Colors.BOLD}🎉 ALL COMPLEX SCENARIOS PASSED!{Colors.ENDC}")

    # ----------------------------------------------------------------
    # PHASE 1: ADMIN SETUP
    # ----------------------------------------------------------------
    
    def step_01_admin_login(self):
        res = requests.post(f"{BASE_URL}/api/auth/login", json={"email": ADMIN_EMAIL, "password": ADMIN_PASS})
        if res.status_code == 200:
            self.admin_token = res.json()["accessToken"]
            print_status("01. Admin Login", f"Success ({ADMIN_EMAIL})", True)
        else:
            print_status("01. Admin Login", f"Failed: {res.text}", False)

    def step_02_create_team(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        payload = {"name": "DevOps Team", "description": "Infrastructure handlers"}
        
        res = requests.post(f"{BASE_URL}/api/Team", json=payload, headers=headers)
        if res.status_code == 200:
            self.team_id = res.json()["id"]
            print_status("02. Create Team", f"Created Team ID: {self.team_id}", True)
        else:
            print_status("02. Create Team", f"Failed: {res.text}", False)

    def step_03_create_sprint(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        payload = {
            "name": "Sprint 42",
            "goal": "Migrate Database",
            "team": {"id": self.team_id}
        }
        res = requests.post(f"{BASE_URL}/api/Sprint", json=payload, headers=headers)
        if res.status_code == 200:
            self.sprint_id = res.json()["id"]
            print_status("03. Create Sprint", f"Created Sprint ID: {self.sprint_id}", True)
        else:
            print_status("03. Create Sprint", f"Failed: {res.text}", False)

    # ----------------------------------------------------------------
    # PHASE 2: USER 1
    # ----------------------------------------------------------------

    def step_04_user1_register_login(self):
        email = generate_random_email()
        pwd = "password123"
        requests.post(f"{BASE_URL}/api/auth/register", json={"email": email, "password": pwd})
        res = requests.post(f"{BASE_URL}/api/auth/login", json={"email": email, "password": pwd})
        self.user1_token = res.json()["accessToken"]
        print_status("04. User 1 Login", f"Logged in as {email}", True)

    def step_05_user1_create_ticket(self):
        headers = {"Authorization": f"Bearer {self.user1_token}"}
        payload = {"title": "Fix Login Bug", "status": "IN_PROGRESS", "sprint": {"id": self.sprint_id}}
        
        res = requests.post(f"{BASE_URL}/api/Ticket", json=payload, headers=headers)
        if res.status_code == 200:
            print_status("05. User 1 Ticket", "Ticket 'Fix Login Bug' created", True)
        else:
            print_status("05. User 1 Ticket", f"Failed: {res.text}", False)

    # ----------------------------------------------------------------
    # PHASE 3: USER 2 (ISOLATION)
    # ----------------------------------------------------------------

    def step_06_user2_register_login(self):
        email = generate_random_email()
        pwd = "password123"
        requests.post(f"{BASE_URL}/api/auth/register", json={"email": email, "password": pwd})
        res = requests.post(f"{BASE_URL}/api/auth/login", json={"email": email, "password": pwd})
        self.user2_token = res.json()["accessToken"]
        print_status("06. User 2 Login", f"Logged in as {email}", True)

    def step_07_data_isolation_check(self):
        """User 2 checks 'My Tickets'. Should be EMPTY, even though User 1 created one."""
        headers = {"Authorization": f"Bearer {self.user2_token}"}
        
        res = requests.get(f"{BASE_URL}/api/Ticket/my", headers=headers)
        data = res.json()
        
        if res.status_code == 200 and len(data) == 0:
            print_status("07. Isolation Check", "PASS: User 2 cannot see User 1's data", True)
        else:
            print_status("07. Isolation Check", f"FAIL: User 2 saw {len(data)} tickets!", False)

    def step_08_user2_create_ticket(self):
        headers = {"Authorization": f"Bearer {self.user2_token}"}
        payload = {"title": "Update CSS", "status": "TODO", "sprint": {"id": self.sprint_id}}
        
        res = requests.post(f"{BASE_URL}/api/Ticket", json=payload, headers=headers)
        if res.status_code == 200:
            print_status("08. User 2 Ticket", "Ticket 'Update CSS' created", True)
        else:
            print_status("08. User 2 Ticket", f"Failed: {res.text}", False)

    # ----------------------------------------------------------------
    # PHASE 4: ADMIN OVERSIGHT
    # ----------------------------------------------------------------

    def step_09_admin_view_all(self):
        """Admin checks 'All Tickets'. Should see BOTH User 1 and User 2 tickets."""
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        
        res = requests.get(f"{BASE_URL}/api/Ticket", headers=headers)
        data = res.json()
        
        # We expect at least 2 tickets (one from User 1, one from User 2)
        if res.status_code == 200 and len(data) >= 2:
            print_status("09. Admin Oversight", f"PASS: Admin sees {len(data)} tickets (All Users)", True)
        else:
            print_status("09. Admin Oversight", f"FAIL: Admin only saw {len(data)} tickets", False)

    def step_10_user_security_boundary(self):
        """User 1 tries to access the 'View All' Admin endpoint. Should fail."""
        headers = {"Authorization": f"Bearer {self.user1_token}"}
        
        # User has 'view-own' but NOT 'view-all'. API /api/Ticket is view-all.
        res = requests.get(f"{BASE_URL}/api/Ticket", headers=headers)
        
        if res.status_code == 403:
             print_status("10. Security Boundary", "PASS: User blocked from Admin View (403)", True)
        else:
             print_status("10. Security Boundary", f"FAIL: User accessed Admin View! (Code {res.status_code})", False)

if __name__ == "__main__":
    ComplexTester().run()