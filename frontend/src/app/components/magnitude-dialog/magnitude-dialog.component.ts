import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface MagnitudeData {
  url: string
}

@Component({
  selector: 'magnitude-dialog',
  templateUrl: './magnitude-dialog.component.html',
  styleUrls: ['./magnitude-dialog.component.css']
})
export class MagnitudeDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: MagnitudeData) {}
}
