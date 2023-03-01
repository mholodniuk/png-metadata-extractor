export interface PNGData {
  chunks: Chunk[];
  errors: string[];
  validPNG: boolean;
};

export interface Chunk {
  type: string;
  length: number;
  rawBytes?: string[];
  properties?: Properties;
  errors?: string[];
  crc: string
};

type Properties = { 
  [id: string]: number; 
};

export type ID = {
  id: string;
}

const IHDR: Chunk = {
  type: 'IHDR',
  properties: {
    'width': 32,
    'height': 32,
    'bit depth': 8,
    'color type': 6,
    'compression method': 0,
    'filter method': 0,
    'interlace method': 0
  },
  length: 13,
  // bytes: [],
  crc: "737A7AF4"
}

const gAMA: Chunk = {
  type: "gAMA",
  length: 4,
  // bytes: [],
  properties: {
    'gamma': 1.000
  },
  crc: "31E8965F"
};

const IDAT: Chunk = {
  type: 'IDAT',
  length: 111,
  // bytes: [],
  // properties: {},
  crc: "160FB84C"
};

const IEND: Chunk = {
  type: "IEND",
  length: 0,
  // bytes: [],
  // properties: {},
  crc: "AE426082"
}