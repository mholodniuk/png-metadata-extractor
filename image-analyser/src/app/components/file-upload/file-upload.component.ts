import { Component, OnInit } from '@angular/core';
import { Observable, catchError, of, switchMap, tap } from 'rxjs';
import { Chunk, PNGData, Properties } from 'src/app/models/PNGData';
import { FileService } from 'src/app/services/file.service';


@Component({
  selector: 'file-upload',
  template: `
  <div class="root-container">
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
                <img [src]="imageURL" style="max-width:100%; max-height:100%; height: auto;" [alt]="fileName">
              </div>
            </div>
          </div>
        </div>
      </mat-card-content>
    </mat-card>
    <hr />
    <div *ngIf="currentFileMetadata$ | async as currentFileMetadata">
      <div class="d-flex justify-content-start mx-5 align-items-center">
        <button mat-stroked-button color="primary" class="action-button" (click)="downloadImage()">Download</button>
        <button mat-raised-button color="warn" class="action-button" (click)="removeAncillaryChunksAction(currentFileMetadata.id)">Remove all ancillary chunks</button>
        <button mat-raised-button color="warn" class="action-button" (click)="removeNonselectedChunksAction(currentFileMetadata.id)">Remove not selected chunks</button>
      </div>
      <hr />
      <div class="d-flex justify-content-center">
        <mat-chip-listbox [multiple]="true">
          <mat-chip-option 
            [selected]="isChunkVisible(chunk)"
            (selectionChange)="changeChunkSelectionStatus(chunk, $event.selected)"
            *ngFor="let chunk of chunksDetected"
          >
            {{chunk}}
          </mat-chip-option>
        </mat-chip-listbox>
      </div>
      <div *ngFor="let chunk of currentFileMetadata.chunks">
        <mat-card *ngIf="isChunkVisible(chunk.type)" class="card wide">
          <mat-card-header>
            <mat-card-title>{{chunk.type}}</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <mat-tab-group animationDuration="10ms">
              <mat-tab label="Overview">
                <mat-list role="list">
                  <mat-list-item role="listitem">Length: {{chunk.length}}</mat-list-item>
                  <mat-list-item role="listitem">CRC: {{chunk.crc}}</mat-list-item>
                </mat-list>
              </mat-tab>
              <mat-tab label="Properties" *ngIf="chunk.type !== 'IEND' && chunk.properties">
              <mat-list role="list">
                  <mat-list-item *ngFor="let property of formatMapToList(chunk.properties)" role="listitem">
                    {{property[0]}}: {{property[1]}}
                  </mat-list-item>
                </mat-list>
              </mat-tab>
              <mat-tab label="Raw bytes" *ngIf="chunk.type !== 'IEND'">
                <code>{{chunk.rawBytes?.join(' ')}}</code>
              </mat-tab>
            </mat-tab-group>
          </mat-card-content>
        </mat-card>
      </div>
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
    .wide {
      width: 70%;
    }
    .root-container {
      height: max-content;
      padding-bottom: 2rem;
    }
    .action-button {
      margin: 0 0.8rem;
    }
  `],
})
export class FileUploadComponent {
  fileName: string;
  currentFileMetadata$: Observable<PNGData>;
  imageURL: string;
  chunksDetected: string[];
  chunksToDisplay: string[];
  reader: FileReader = new FileReader();

  constructor(private fileUploadService: FileService) { }

  onFileSelected(event: Event) {
    const target: HTMLInputElement = event.target as HTMLInputElement;
    if (!target.files)
      return;

    const file: File = target.files[0];

    if (file) {
      this.fileName = (file as File).name;
      this.saveImageFromBlob(file);

      this.currentFileMetadata$ = this.fileUploadService.uploadFile(file)
        .pipe(
          switchMap((id: string) => this.fileUploadService.getImageMetadata(id)),
          tap((metadata: PNGData) => {
            this.chunksDetected = [...new Set(metadata.chunks.map((chunk: Chunk) => chunk.type))];
            this.chunksToDisplay = [...this.chunksDetected];
          }),
          catchError((error: any) => {
            console.log(error);
            return of(error)
          })
        );
    }
  }

  changeChunkSelectionStatus(chunk: string, isSelected: boolean): void {
    if (isSelected)
      this.onChunkSelected(chunk);
    else
      this.onChunkDeselected(chunk);
  }

  onChunkSelected(chunk: string): void {
    if (this.chunksToDisplay.includes(chunk))
      return;
    this.chunksToDisplay.push(chunk);
  }

  onChunkDeselected(chunk: string): void {
    const idx = this.chunksToDisplay.indexOf(chunk);
    if (idx < 0) 
      return;
    this.chunksToDisplay.splice(idx, 1);
  }

  isChunkVisible(chunk: string): boolean {
    return this.chunksToDisplay.includes(chunk);
  }

  saveImageFromBlob(file: Blob): void {
    this.reader.onload = () => {
      this.imageURL = this.reader.result as string;
    }
    this.reader.readAsDataURL(file);
  }

  removeAncillaryChunksAction(id: string): void {
    this.currentFileMetadata$ = this.fileUploadService.removeAncillaryChunks(id)
        .pipe(
          tap((file: Blob) => this.saveImageFromBlob(file)),
          switchMap(() => this.fileUploadService.getImageMetadata(id)),
          tap((metadata: PNGData) => {
            this.chunksDetected = [...new Set(metadata.chunks.map((chunk: Chunk) => chunk.type))];
            this.chunksToDisplay = [...this.chunksDetected];
          }),
          catchError((error: any) => {
            console.log(error);
            return of(error)
          })
        );
  }

  removeNonselectedChunksAction(id: string): void {
    console.log("(removeNonselectedChunksAction) NOT IMPLEMENTED");
  }

  downloadImage(): void {
    console.log("(downloadImage) NOT IMPLEMENTED");
  }

  formatMapToList(props: Properties | undefined): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
