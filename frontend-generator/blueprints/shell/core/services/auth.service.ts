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

  // 1. REGISTER: Expects TEXT because Controller returns "Registration successful" string
  register(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, credentials, { responseType: 'text' });
  }

  // 2. LOGIN: Expects JSON because Service returns 'new AuthResponse(token)'
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

  getUserRole(): string {
    const token = this.getToken();
    if (!token) return '';
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded.role || decoded.authorities || 'USER'; 
    } catch (e) {
      return '';
    }
  }

  hasPermission(allowedRoles: string[]): boolean {
    const userRole = this.getUserRole();
    if (!userRole) return false;
    if (allowedRoles.includes('ANY')) return true;
    return allowedRoles.includes(userRole);
  }
}