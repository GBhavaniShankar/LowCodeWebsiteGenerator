import requests
import json
import random
import string
import sys

# ==========================================
# CONFIGURATION
# ==========================================
BASE_URL = "http://localhost:8080"
ADMIN_EMAIL = "admin@test.com"
ADMIN_PASSWORD = "secretpassword"

# Colors for terminal output
class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
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
    """Generates a random email to allow repeated testing"""
    random_str = ''.join(random.choices(string.ascii_lowercase + string.digits, k=8))
    return f"user_{random_str}@test.com"

class APITester:
    def __init__(self):
        self.session = requests.Session()
        self.admin_token = None
        self.user_token = None
        self.test_user_email = generate_random_email()
        self.test_user_password = "password123"

    def run_all_tests(self):
        print(f"{Colors.HEADER}Starting API Verification Tests...{Colors.ENDC}")
        print(f"{Colors.OKCYAN}Target: {BASE_URL}{Colors.ENDC}\n")

        self.test_1_admin_login()
        self.test_2_admin_create_category()
        self.test_3_admin_get_categories()
        self.test_4_register_user()
        self.test_5_login_user()
        self.test_6_user_get_categories()
        self.test_7_user_create_category_fail()

        print(f"\n{Colors.OKGREEN}{Colors.BOLD}🎉 ALL TESTS PASSED SUCCESSFULLY!{Colors.ENDC}")

    # ==========================================
    # TESTS
    # ==========================================

    def test_1_admin_login(self):
        """Step 1: Login as Admin"""
        url = f"{BASE_URL}/api/auth/login"
        payload = {"email": ADMIN_EMAIL, "password": ADMIN_PASSWORD}
        
        try:
            res = self.session.post(url, json=payload)
            if res.status_code == 200:
                self.admin_token = res.json().get("accessToken")
                print_status("1. Admin Login", f"Logged in as {ADMIN_EMAIL}", True)
            else:
                print_status("1. Admin Login", f"Failed with {res.status_code}: {res.text}", False)
        except requests.exceptions.ConnectionError:
            print_status("1. Admin Login", "Could not connect to server. Is it running?", False)

    def test_2_admin_create_category(self):
        """Step 2: Admin Creates a Category"""
        url = f"{BASE_URL}/api/Category"
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        payload = {
            "name": "Electronics",
            "description": "Gadgets and Stuff"
        }
        
        res = requests.post(url, json=payload, headers=headers)
        if res.status_code == 200:
            print_status("2. Admin Create", "Created 'Electronics' category", True)
        else:
            print_status("2. Admin Create", f"Failed {res.status_code}: {res.text}", False)

    def test_3_admin_get_categories(self):
        """Step 3: Admin Gets All Categories"""
        url = f"{BASE_URL}/api/Category"
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        
        res = requests.get(url, headers=headers)
        if res.status_code == 200:
            data = res.json()
            if isinstance(data, list) and len(data) > 0:
                print_status("3. Admin Read", f"Retrieved {len(data)} categories", True)
            else:
                print_status("3. Admin Read", "List is empty or invalid format", False)
        else:
            print_status("3. Admin Read", f"Failed {res.status_code}", False)

    def test_4_register_user(self):
        """Step 4: Register a New User"""
        url = f"{BASE_URL}/api/auth/register"
        payload = {"email": self.test_user_email, "password": self.test_user_password}
        
        res = requests.post(url, json=payload)
        # 200 OK is expected. If it's 400, it might be validation or duplicate email
        if res.status_code == 200:
            print_status("4. Register User", f"Registered {self.test_user_email}", True)
        else:
            print_status("4. Register User", f"Failed {res.status_code}: {res.text}", False)

    def test_5_login_user(self):
        """Step 5: Login as New User"""
        url = f"{BASE_URL}/api/auth/login"
        payload = {"email": self.test_user_email, "password": self.test_user_password}
        
        res = requests.post(url, json=payload)
        if res.status_code == 200:
            self.user_token = res.json().get("accessToken")
            print_status("5. User Login", "Got User Access Token", True)
        else:
            print_status("5. User Login", f"Failed {res.status_code}: {res.text}", False)

    def test_6_user_get_categories(self):
        """Step 6: User Reads Categories (Allowed)"""
        url = f"{BASE_URL}/api/Category"
        headers = {"Authorization": f"Bearer {self.user_token}"}
        
        res = requests.get(url, headers=headers)
        if res.status_code == 200:
            print_status("6. User Read", "User allowed to view categories", True)
        else:
            print_status("6. User Read", f"Failed {res.status_code} (Should be 200)", False)

    def test_7_user_create_category_fail(self):
        """Step 7: User Tries to Create Category (Should Fail)"""
        url = f"{BASE_URL}/api/Category"
        headers = {"Authorization": f"Bearer {self.user_token}"}
        payload = {
            "name": "Hacking Tools",
            "description": "This should be forbidden"
        }
        
        res = requests.post(url, json=payload, headers=headers)
        
        # We EXPECT a 403 Forbidden here
        if res.status_code == 403:
            print_status("7. User Create", "Correctly blocked (403 Forbidden)", True)
        else:
            print_status("7. User Create", f"Security Failure! User was able to create (Code {res.status_code})", False)

if __name__ == "__main__":
    tester = APITester()
    tester.run_all_tests()