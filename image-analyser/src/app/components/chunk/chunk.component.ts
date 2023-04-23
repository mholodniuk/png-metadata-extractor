import { Component, Input } from '@angular/core';
import { Chunk, Properties } from 'src/app/models/PNGData';

@Component({
  selector: 'chunk',
  templateUrl: './chunk.component.html',
  styleUrls: ['./chunk.component.css']
})
export class ChunkComponent {

  @Input()
  chunk: Chunk

  @Input()
  isVisible: boolean | null

  formatMapToList(props?: Properties): [string, unknown][] {
    if (props == undefined) return [];
    return Object.keys(props).map((key: string) => [key, props[key]]);
  }
}
