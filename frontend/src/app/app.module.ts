import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { HttpClientModule } from '@angular/common/http';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { ChunkComponent } from './components/chunk/chunk.component';
import { MagnitudeDialogComponent } from './components/magnitude-dialog/magnitude-dialog.component';
import { ErrorSnackbarComponent } from './components/error-snackbar/error-snackbar.component';

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent,
    ChunkComponent,
    MagnitudeDialogComponent,
    ErrorSnackbarComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    HttpClientModule,
    MatTabsModule,
    MatListModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
