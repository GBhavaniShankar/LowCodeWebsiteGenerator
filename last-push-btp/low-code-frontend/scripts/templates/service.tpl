import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { {{ResourceName}} } from './{{ResourceNameLower}}.model';

@Injectable({ providedIn: 'root' })
export class {{ResourceName}}Service {
  private apiUrl = `${environment.apiUrl}/api/{{ResourceName}}`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<{{ResourceName}}[]> {
    return this.http.get<{{ResourceName}}[]>(this.apiUrl);
  }

  getMy(): Observable<{{ResourceName}}[]> {
    return this.http.get<{{ResourceName}}[]>(`${this.apiUrl}/my`);
  }

  create(item: {{ResourceName}}): Observable<{{ResourceName}}> {
    return this.http.post<{{ResourceName}}>(this.apiUrl, item);
  }
}