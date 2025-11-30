import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

interface AuthResponse {
  accessToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private tokenKey = 'auth_token';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(!!this.getToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  register(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, credentials);
  }

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem(this.tokenKey, response.accessToken);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  // --- NEW: Helper to get the Role from the Token ---
  getUserRole(): string {
    const token = this.getToken();
    if (!token) return '';

    try {
      // Simple JWT decode (Base64)
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      
      // NOTE: We assume the backend puts the role in 'role' or 'authorities' claim.
      // If your backend doesn't support this yet, this might return undefined.
      // You might need to ask your teammate to add .claim("role", "ADMIN") to the JWT.
      return decoded.role || decoded.authorities || 'USER'; 
    } catch (e) {
      return '';
    }
  }

  // Helper to check permissions
  hasPermission(allowedRoles: string[]): boolean {
    const userRole = this.getUserRole();
    if (!userRole) return false;
    // If 'ANY' is passed, just checking if logged in
    if (allowedRoles.includes('ANY')) return true;
    return allowedRoles.includes(userRole);
  }
}