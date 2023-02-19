package com.dev.imageprocessingapi.model;

import com.dev.imageprocessingapi.model.chunks.Chunk;
import lombok.ToString;

import java.util.List;

@ToString
public class PNGImage {
    List<Chunk> chunks;
}
