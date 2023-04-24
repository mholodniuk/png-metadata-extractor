import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Properties } from 'src/app/models/PNGData';
import { saveAs } from 'file-saver';
import { MetadataStore } from '../../store/metadata.store';
import { MatDialog } from '@angular/material/dialog';
import { MagnitudeDialogComponent } from '../magnitude-dialog/magnitude-dialog.component';
import { FileService } from 'src/app/services/file.service';


@Component({
  selector: 'file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css'],
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

  constructor(private store: MetadataStore, private dialog: MatDialog, private service: FileService) {}

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
    if (isSelected) {
      this.store.addChunkToSelected(chunk);
    } else {
      this.store.removeChunkFromSelected(chunk);
    }
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

  openMagnitudeDialog(id: string): void {
    this.service.getImageMagnitude(id).subscribe(file => {
      const url = window.URL.createObjectURL(file);
      this.dialog.open(MagnitudeDialogComponent, {
        data: {
          url: url,
        }
      });
    })
  }

  formatMapToList(props?: Properties): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
