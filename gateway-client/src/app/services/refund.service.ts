import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Refund {
  id?: number;
  amount: number;
  status: string;
  requestDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class RefundService {
  private baseUrl = 'http://localhost:8093/refund';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwR0xUX3YyUTU3QW5uNHpJaWt1UWZCb3FSTkItVXI1SUE3cFpNTDVOVWVrIn0.eyJleHAiOjE3NDQ4MTU1NzQsImlhdCI6MTc0NDM4MzU3NCwianRpIjoiMGRiODhmOGItNTU5Mi00NjZiLTg5ZjEtOWE5NDA1MmRiODQ3IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3JlZnVuZC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJjZDBiNWQ5YS0zMTg4LTQzMzAtYTg2Ni0zZGNmYTczNWNkNGYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBcGlnYXR3YXk0c2FlMTAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtcmVmdW5kLXJlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgb2ZmbGluZV9hY2Nlc3MgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SG9zdCI6IjE3Mi4xOC4wLjEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtYXBpZ2F0d2F5NHNhZTEwIiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4xOC4wLjEiLCJjbGllbnRfaWQiOiJBcGlnYXR3YXk0c2FlMTAifQ.HvbycXOCrcgmRjjgL3vkvr5OCdmulhLPAzxr-uh14oxr0AzFmD6kgZOB2jY4FzZicmCf2ipd9pNgZVseNzwQ3kjDdeNAYTo24HUvcdiFApFvNIfWlmYYFatoU7f3vlooMGvkEVJFyuqY3-0DSHPBU3rGN_u0EZQKcHQkl5oGe9Av1lRr4DOhhjqoRqMfwYEbh58qx1jO_Bw_K-r2wgDvj_5jBRNwHuf8unNIjtvwjPBjeXO7c1rmQXggQZf5alfO1-6Gd5dqshwx3TJLLgNXe9eospyYlqsaHJh8TR9tmUYstfMNJNibG2yKC6phOCnqEatvp3RWeBnH66jmM3DnBQ');
    return new HttpHeaders({
      'Authorization': `Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwR0xUX3YyUTU3QW5uNHpJaWt1UWZCb3FSTkItVXI1SUE3cFpNTDVOVWVrIn0.eyJleHAiOjE3NDQ4MTU1NzQsImlhdCI6MTc0NDM4MzU3NCwianRpIjoiMGRiODhmOGItNTU5Mi00NjZiLTg5ZjEtOWE5NDA1MmRiODQ3IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3JlZnVuZC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJjZDBiNWQ5YS0zMTg4LTQzMzAtYTg2Ni0zZGNmYTczNWNkNGYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBcGlnYXR3YXk0c2FlMTAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtcmVmdW5kLXJlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgb2ZmbGluZV9hY2Nlc3MgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SG9zdCI6IjE3Mi4xOC4wLjEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtYXBpZ2F0d2F5NHNhZTEwIiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4xOC4wLjEiLCJjbGllbnRfaWQiOiJBcGlnYXR3YXk0c2FlMTAifQ.HvbycXOCrcgmRjjgL3vkvr5OCdmulhLPAzxr-uh14oxr0AzFmD6kgZOB2jY4FzZicmCf2ipd9pNgZVseNzwQ3kjDdeNAYTo24HUvcdiFApFvNIfWlmYYFatoU7f3vlooMGvkEVJFyuqY3-0DSHPBU3rGN_u0EZQKcHQkl5oGe9Av1lRr4DOhhjqoRqMfwYEbh58qx1jO_Bw_K-r2wgDvj_5jBRNwHuf8unNIjtvwjPBjeXO7c1rmQXggQZf5alfO1-6Gd5dqshwx3TJLLgNXe9eospyYlqsaHJh8TR9tmUYstfMNJNibG2yKC6phOCnqEatvp3RWeBnH66jmM3DnBQ`,
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Connection': 'keep-alive',
      'Accept-Encoding': 'gzip, deflate, br',
    });
  }
  

  getRefunds(): Observable<Refund[]> {
    return this.http.get<Refund[]>(this.baseUrl, { headers: this.getAuthHeaders() });
  }

  addRefund(refund: Refund): Observable<Refund> {
    return this.http.post<Refund>(this.baseUrl, refund, { headers: this.getAuthHeaders() });
  }

  updateRefund(id: number, refund: Refund): Observable<Refund> {
    return this.http.put<Refund>(`${this.baseUrl}/${id}`, refund, { headers: this.getAuthHeaders() });
  }

  deleteRefund(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
  downloadRefundPdf(): Observable<Blob> {
    return this.http.get('http://localhost:8093/refund/export/pdf', {
      headers: this.getAuthHeaders(),
      responseType: 'blob'
    });
  }
  downloadRefundPdfFiltered(status?: string): Observable<Blob> {
    let url = 'http://localhost:8093/refund/export/pdf/tri';
    if (status) {
      headers: this.getAuthHeaders(),
      url += `?status=${status}`;
    }
    return this.http.get(url, { headers: this.getAuthHeaders(), responseType: 'blob' });
  }
    
 
  /*

  getRefunds(): Observable<Refund[]> {
    return this.http.get<Refund[]>(this.baseUrl);
  }

  addRefund(refund: Refund): Observable<Refund> {
    return this.http.post<Refund>(this.baseUrl, refund);
  }

  updateRefund(id: number, refund: Refund): Observable<Refund> {
    return this.http.put<Refund>(`${this.baseUrl}/${id}`, refund);
  }

  deleteRefund(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  downloadRefundPdf(): Observable<Blob> {
    return this.http.get('http://localhost:8093/refund/export/pdf', {
      responseType: 'blob'
    });
  }
  downloadRefundPdfFiltered(status?: string): Observable<Blob> {
    let url = 'http://localhost:8093/refund/export/pdf/tri';
    if (status) {
      url += `?status=${status}`;
    }
    return this.http.get(url, { responseType: 'blob' });
  }
  */

}
