import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ID, PNGData } from '../models/PNGData';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private apiUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  uploadFile(file: File): Observable<string> {
    const formData = new FormData();
    formData.append("file", file);
    return this.http.post(`${this.apiUrl}/upload-image` , formData, { responseType: "text" });
  }

  getImageMetadata(id: string): Observable<PNGData> {
    return this.http.get<PNGData>(`${this.apiUrl}/metadata/${id}`);
  }
}
