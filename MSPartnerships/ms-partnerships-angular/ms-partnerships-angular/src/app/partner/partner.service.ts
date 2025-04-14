import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timeout, catchError } from 'rxjs';
import { Partner } from './partner.model';

@Injectable({
  providedIn: 'root'
})
export class PartnerService {
  private baseUrl = 'http://localhost:8093/api/partners';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    withCredentials: false
  };
  private requestTimeout = 10000; // 10 seconds

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      return throwError(() => new Error('Cannot connect to server.'));
    }
    return throwError(() => new Error(error.error?.message || `Error ${error.status}: ${error.statusText}`));
  }

  getAllPartners(): Observable<Partner[]> {
    return this.http.get<Partner[]>(this.baseUrl, this.httpOptions)
      .pipe(timeout(this.requestTimeout), catchError(this.handleError));
  }

  createPartner(partner: Partner): Observable<Partner> {
    return this.http.post<Partner>(this.baseUrl, partner, this.httpOptions)
      .pipe(timeout(this.requestTimeout), catchError(this.handleError));
  }

  updatePartner(id: number, partner: Partner): Observable<Partner> {
    return this.http.put<Partner>(`${this.baseUrl}/${id}`, partner, this.httpOptions)
      .pipe(timeout(this.requestTimeout), catchError(this.handleError));
  }

  deletePartner(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, this.httpOptions)
      .pipe(timeout(this.requestTimeout), catchError(this.handleError));
  }

  getPartnerById(id: number): Observable<Partner> {
    return this.http.get<Partner>(`${this.baseUrl}/${id}`, this.httpOptions)
      .pipe(timeout(this.requestTimeout), catchError(this.handleError));
  }
}
