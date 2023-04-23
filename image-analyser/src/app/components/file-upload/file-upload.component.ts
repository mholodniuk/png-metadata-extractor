import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Properties } from 'src/app/models/PNGData';
import { saveAs } from 'file-saver';
import { MetadataStore } from './store/metadata.store';

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
                    <span>{{ fileName || '' }}</span>
                  </div>
                  <div class="p-2">
                    <button mat-mini-fab color="primary" (click)="fileUpload.click()">
                      <mat-icon>attach_file</mat-icon>
                    </button>
                  </div>
                </div>
                <input type="file" class="file-input" (change)="onFileSelected($event)" #fileUpload/>
              </div>
            </div>
            <div class="row justify-content-center">
              <div class="col-md-12">
                <div *ngIf="imageURL" class="text-center">
                  <img [src]="imageURL" style="max-width:100%; max-height:100%; height: auto;" [alt]="fileName"/>
                </div>
              </div>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
      <hr style="margin: 1rem;" />
      <div *ngIf="currentFileMetadata$ | async as currentFileMetadata">
        <div class="d-flex justify-content-start mx-5 align-items-center">
          <button mat-stroked-button color="primary" class="action-button" (click)="downloadImage()">
            Download
          </button>
          <button mat-raised-button color="warn" class="action-button" (click)="removeAncillaryChunks(currentFileMetadata.id)">
            Remove all ancillary chunks
          </button>
          <button mat-raised-button color="warn" class="action-button" (click)="removeNotSelectedChunks(currentFileMetadata.id)">Remove not selected chunks</button>
        </div>
        <hr style="margin: 1rem;" />
        <div class="d-flex justify-content-center">
          <mat-chip-listbox [multiple]="true" style="margin: 0 1rem;">
            <mat-chip-option [selected]="isChunkVisible(chunk) | async" 
              (selectionChange)="changeChunkSelectionStatus(chunk, $event.selected)"
              *ngFor="let chunk of chunksDetected$ | async"
            >
              {{ chunk }}
            </mat-chip-option>
          </mat-chip-listbox>
        </div>
        <div *ngFor="let chunk of currentFileMetadata.chunks">
          <chunk [chunk]="chunk" [isVisible]="isChunkVisible(chunk.type) | async" />
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .file-input {
        display: none;
      }
      .card {
        width: 50%;
        margin: auto;
        margin-top: 2rem;
      }
      .root-container {
        height: max-content;
        padding-bottom: 2rem;
      }
      .action-button {
        margin: 0 0.8rem;
      }
      .color-box {
        width: 50px;
        height: 50px;
        margin: 5px;
        display: inline-block;
        border: 1px solid black;
      }
    `,
  ],
  providers: [MetadataStore],
})
export class FileUploadComponent {
  currentFileMetadata$ = this.store.metadata$;
  chunksDetected$ = this.store.chunksDetected$;
  chunksToDisplay$ = this.store.chunksToDisplay$;
  notSelectedChunks$ = this.store.notSelectedChunks$;
  error$ = this.store.error$;
  fileName: string;
  imageURL: string;
  reader: FileReader = new FileReader();

  constructor(private store: MetadataStore) {}

  onFileSelected(event: Event) {
    const target: HTMLInputElement = event.target as HTMLInputElement;
    if (!target.files) return;
    const file: File = target.files[0];

    if (file) {
      this.fileName = (file as File).name;
      this.saveImageFromBlob(file);
      this.store.uploadImage(file);
    }
  }

  isChunkVisible(chunk: string): Observable<boolean> {
    return this.store.isChunkVisible$(chunk);
  }

  changeChunkSelectionStatus(chunk: string, isSelected: boolean): void {
    if (isSelected) this.store.addChunkToSelected(chunk);
    else this.store.removeChunkFromSelected(chunk);
  }

  saveImageFromBlob(file: Blob): void {
    this.reader.onload = () => {
      this.imageURL = this.reader.result as string;
    };
    this.reader.readAsDataURL(file);
  }

  removeAncillaryChunks(id: string): void {
    this.store.removeAncillaryChunks(id);
  }

  removeNotSelectedChunks(id: string): void {
    this.store.removeNotSelectedChunks(id);
  }

  downloadImage(): void {
    saveAs(this.imageURL, this.fileName);
  }

  formatMapToList(props?: Properties): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
