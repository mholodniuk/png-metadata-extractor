import { Chunk, PNGData } from "../models/PNGData";


export function initializeChunks(metadata: PNGData): string[] {
  return [...new Set(metadata.chunks.map((chunk: Chunk) => chunk.type))];
}