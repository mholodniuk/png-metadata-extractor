import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { PNGData, mockPNG } from '../models/PNGData';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private apiUrl: string = 'http://localhost:8080/images';

  constructor(private http: HttpClient) { }

  uploadFile(file: File): Observable<string> {
    const formData = new FormData();
    formData.append("file", file);
    return this.http.post(`${this.apiUrl}` , formData, { responseType: "text" });
  }

  mockUploadFile(file: File): Observable<string> {
    return of('123456789');
  }

  getImageMetadata(id: string): Observable<PNGData> {
    return this.http.get<PNGData>(`${this.apiUrl}/${id}/metadata`);
  }

  getMockImageMetadata(id: string): Observable<PNGData> {
    return of(mockPNG);
  }
}
