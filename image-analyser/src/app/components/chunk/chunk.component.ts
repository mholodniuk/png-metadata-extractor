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

  ngOnInit(): void {
    if (this.chunk.type === 'PLTE') {
      this.readPLTE(this.chunk.properties);
    }
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
