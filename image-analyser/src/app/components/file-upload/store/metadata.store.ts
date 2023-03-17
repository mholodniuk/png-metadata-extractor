import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { EMPTY, Observable } from 'rxjs';
import { catchError, concatMap, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { PNGData } from 'src/app/models/PNGData';
import { FileService } from 'src/app/services/file.service';
import { initializeChunks } from 'src/app/utils/utils';

interface MetadataState {
  metadata?: PNGData;
  error?: string;
  chunksDetected: string[];
  chunksToDisplay: string[];
}

@Injectable()
export class MetadataStore extends ComponentStore<MetadataState> {
  readonly metadata$ = this.select((state) => state.metadata);
  readonly chunksDetected$ = this.select((state) => state.chunksDetected);
  readonly chunksToDisplay$ = this.select((state) => state.chunksToDisplay);
  readonly notSelectedChunks$ = this.select((state) => 
    state.chunksDetected.filter((chunk: string) => state.chunksToDisplay.indexOf(chunk) < 0)
  );
  readonly error$ = this.select((state) => state.error);
  readonly isChunkVisible$ = (chunk: string) =>
    this.select((state) => state.chunksToDisplay.includes(chunk));

  readonly removeChunkFromSelected = this.updater(
    (state, chunkToDelete: string) => ({
      ...state,
      chunksToDisplay: [
        ...state.chunksToDisplay.filter((chunk) => chunk !== chunkToDelete),
      ],
    })
  );
  readonly addChunkToSelected = this.updater((state, chunkToAdd: string) => ({
    ...state,
    chunksToDisplay: [...state.chunksToDisplay, chunkToAdd],
  }));

  readonly uploadImage = this.effect((file$: Observable<File>) => {
    return file$.pipe(
      concatMap((file: File) => {
        return this.fileService.uploadFile(file).pipe(
          tap({
            error: (error) => this.patchState({ error: error }),
          }),
          switchMap((id) => this.fileService.getImageMetadata(id)),
          tap({
            next: (metadata) =>
              this.patchState({
                metadata: metadata,
                chunksDetected: initializeChunks(metadata),
                chunksToDisplay: initializeChunks(metadata),
              }),
            error: (error) => this.patchState({ error: error }),
          }),
          catchError(() => EMPTY)
        );
      })
    );
  });

  readonly removeAncillaryChunks = this.effect((id$: Observable<string>) => {
    return id$.pipe(
      concatMap((id: string) =>
        this.fileService.removeAncillaryChunks(id).pipe(
          switchMap(() => this.fileService.getImageMetadata(id)),
          tap({
            next: (metadata) =>
              this.patchState({
                metadata: metadata,
                chunksDetected: initializeChunks(metadata),
                chunksToDisplay: initializeChunks(metadata),
              }),
            error: (error) => this.patchState({ error: error }),
          }),
          catchError(() => EMPTY)
        )
      )
    );
  });

  readonly removeNotSelectedChunks = this.effect((id$: Observable<string>) => {
    return id$.pipe(
      withLatestFrom(this.notSelectedChunks$),
      concatMap(([id, chunks]) => 
        this.fileService.removeSelectedChunks(id, chunks).pipe(
          switchMap(() => this.fileService.getImageMetadata(id)),
          tap({
            next: (metadata) =>
              this.patchState({
                metadata: metadata,
                chunksDetected: initializeChunks(metadata),
                chunksToDisplay: initializeChunks(metadata),
              }),
            error: (error) => this.patchState({ error: error }),
          }),
          catchError(() => EMPTY)
        )
      )
    );
  });

  constructor(private fileService: FileService) {
    super({
      metadata: undefined,
      error: undefined,
      chunksDetected: [],
      chunksToDisplay: [],
    });
  }
}
