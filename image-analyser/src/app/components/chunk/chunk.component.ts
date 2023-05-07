import { Component, Input, OnInit } from '@angular/core';
import { Chunk, Properties } from 'src/app/models/PNGData';

type RGB = {
  red: number;
  green: number;
  blue: number;
};

@Component({
  selector: 'chunk',
  templateUrl: './chunk.component.html',
  styleUrls: ['./chunk.component.css'],
})
export class ChunkComponent implements OnInit {
  @Input()
  chunk: Chunk;
  palette: RGB[];
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

  displayRGB(color: RGB) {
    return `RGB(${color.red}, ${color.green}, ${color.blue})`;
  }

  adjustStringColor(color: RGB): string {
    return color.red > 220 && color.green > 220 && color.blue > 220
      ? 'black'
      : 'white';
  }

  readPLTE(props?: Properties): any {
    if (props == undefined) return [];
    this.palette = props['Palette'] as RGB[];
  }

  readtIME(properties: Properties): string {
    const day = properties['Day'];
    const month = properties['Month'];
    const year = properties['Year'];
    const hour = properties['Hour'];
    const minute = properties['Minute'];
    const second = properties['Second'];
    return `${day}.${month}.${year} ${hour}:${minute}:${second}`;
  }

  formatMapToList(props?: Properties): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
