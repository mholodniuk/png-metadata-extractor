import { Component, OnInit, Inject } from '@angular/core';
import {
  MatSnackBarRef,
  MAT_SNACK_BAR_DATA,
} from '@angular/material/snack-bar';

@Component({
  selector: 'app-basic-snackbar',
  templateUrl: './error-snackbar.component.html',
  styleUrls: ['./error-snackbar.component.css'],
})
export class ErrorSnackbarComponent implements OnInit {
  constructor(
    public sbRef: MatSnackBarRef<ErrorSnackbarComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public data: any
  ) {}
  ngOnInit() {}
}
