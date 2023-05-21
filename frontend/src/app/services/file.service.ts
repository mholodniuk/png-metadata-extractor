import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, of } from 'rxjs';
import { PNGData, mockPNG } from '../models/PNGData';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private apiUrl: string = 'http://localhost:8080/images';

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}`, formData, { responseType: 'text' });
  }

  removeAncillaryChunks(id: string): Observable<object> {
    return this.http.patch(`${this.apiUrl}/${id}`, {});
  }

  removeSelectedChunks(id: string, chunks: string[]): Observable<object> {
    return this.http.patch(`${this.apiUrl}/${id}`, {
      chunks: chunks,
    });
  }

  getImage(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}`, { responseType: 'blob' });
  }

  getImageMagnitude(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/magnitude`, {
      responseType: 'blob',
    });
  }

  getImagePhase(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/phase`, {
      responseType: 'blob',
    });
  }

  getImageMetadata(id: string): Observable<PNGData> {
    return this.http.get<PNGData>(`${this.apiUrl}/${id}/metadata`);
  }
}
