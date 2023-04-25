import { Component, Input, OnInit } from '@angular/core';
import { Chunk, Properties } from 'src/app/models/PNGData';

interface PLTE {
  red: number;
  green: number;
  blue: number;
}

@Component({
  selector: 'chunk',
  templateUrl: './chunk.component.html',
  styleUrls: ['./chunk.component.css'],
})
export class ChunkComponent implements OnInit {
  @Input()
  chunk: Chunk;
  palette: PLTE[];
  specialChunks = ['PLTE'];
  isHorizontal = false;

  ngOnInit(): void {
    if (this.chunk.type === 'PLTE') {
      this.readPLTE(this.chunk.properties);
    }
  }

  toggleVerticalHorizontal(): void {
    this.isHorizontal = !this.isHorizontal;
  }

  displayRGB(color: PLTE) {
    return `RGB(${color.red}, ${color.green}, ${color.blue})`;
  }

  isWhite(color: PLTE): boolean {
    return color.red === 255 && color.green === 255 && color.blue === 255;
  }

  readPLTE(props?: Properties): any {
    if (props == undefined) return [];
    this.palette = props['Palette'] as PLTE[];
  }

  formatMapToList(props?: Properties): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
