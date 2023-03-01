import { Component, OnInit } from '@angular/core';
import { Observable, catchError, of, switchMap, tap } from 'rxjs';
import { PNGData } from 'src/app/models/PNGData';
import { FileService } from 'src/app/services/file.service';

// https://medium.com/@tarekabdelkhalek/how-to-create-a-drag-and-drop-file-uploading-in-angular-78d9eba0b854
// drop an image

@Component({
  selector: 'file-upload',
  template: `
  <div>
    <mat-card class="card">
      <mat-card-header>
        <mat-card-title>Uplaod your image</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="container-fluid">
          <div class="row align-items-center">
            <div class="col-md-12">
              <div class="d-flex flex-row align-items-center">
                <div class="p-2">
                  <span>{{ fileName || "No file uploaded yet." }}</span>
                </div>
                <div class="p-2">
                  <button mat-mini-fab color="primary" color="primary" (click)="fileUpload.click()">
                    <mat-icon>attach_file</mat-icon>
                  </button>
                </div>
              </div>
              <input type="file" class="file-input" (change)="onFileSelected($event)" #fileUpload>
            </div>
          </div>
          <div class="row justify-content-center">
            <div class="col-md-12">
              <div *ngIf="imageURL" class="text-center">
                <img [src]="imageURL" style="max-width: 400px;" [alt]="fileName">
              </div>
            </div>
          </div>
        </div>
      </mat-card-content>
    </mat-card>
    <hr />
    <div *ngIf="currentFileMetadata$ | async as currentFileMetadata" class="chunk-container">
      <mat-card *ngFor="let chunk of currentFileMetadata.chunks" class="card">
      <mat-card-header>
        <mat-card-title>{{chunk.type}}</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        {{chunk.length}}
        {{chunk.crc}}
      </mat-card-content>
      </mat-card>
    </div>
  </div>
  `,
  styles: [`
    .file-input {
      display: none;
    }
    .card {
      width: 50%; 
      margin: auto; 
      margin-top: 2rem
    }
  `],
})
export class FileUploadComponent implements OnInit {
  fileName: string = '';
  currentFileMetadata$: Observable<PNGData>;
  imageURL: string = '';

  constructor(private fileUploadService: FileService) { }

  ngOnInit(): void {
    console.log("init")
  }

  // TODO:
  // Tabs -> jeden widok dane o zdjęciu, drugi samo zdjęcie
  // zdjecia na cardach
  // feedback uploadu zdjecia za pomocą snackbara
  // modyfikacja daty (?) w zdjeciu -> datepicker
  // chekboxy/multiselect dla info, ktore chcemy wyswietlic (ktore chunki)
  // progress bar/spinner dla uploadu zdjecia
  // strona dla przykladowych innych danych

  onFileSelected(event: Event) {
    const target: HTMLInputElement = event.target as HTMLInputElement;
    if (!target.files)
      return;

    const file: File = target.files[0];

    if (file) {
      this.fileName = file.name;

      const reader = new FileReader();
      reader.onload = () => {
        this.imageURL = reader.result as string;
      }
      reader.readAsDataURL(file);

      this.currentFileMetadata$ = this.fileUploadService.uploadFile(file)
        .pipe(
          switchMap((id: string) => this.fileUploadService.getImageMetadata(id)),
          tap((metadata: PNGData) => console.log(metadata)),
          catchError((error: any) => {
            console.log(error);
            return of(error)
          })
        );
    }
  }
}