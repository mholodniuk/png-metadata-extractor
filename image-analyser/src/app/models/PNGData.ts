export interface PNGData {
  id: string;
  chunks: Chunk[];
  validPNG: boolean;
};

export interface Chunk {
  type: ChunkType;
  length: number;
  rawBytes?: string[];
  properties?: Properties;
  CRC: string;
  isCritical: boolean;
  isPublic: boolean;
  isReserved: boolean,
  isUnsafeToCopy: boolean
};

export type Properties = { 
  [id: string]: unknown; 
};

export type ChunkType = 'IDAT' | 'IHDR' | 'gAMA' | 'IEND';

const IHDR: Chunk = {
  type: "IHDR",
  length: 13,
  rawBytes: [
    "00",
    "00",
    "00",
    "20",
    "00",
    "00",
    "00",
    "20",
    "08",
    "06",
    "00",
    "00",
    "00"
  ],
  properties: {
    'Width': 560,
    'Height': 420,
    'Bit depth': 8,
    'Color type': 'RGBA'
  },
  CRC: "737a7af4",
  isCritical: false,
  isPublic: false,
  isReserved: false,
  isUnsafeToCopy: false
}

const gAMA: Chunk = {
  type: "gAMA",
  length: 4,
  rawBytes: [
    "00",
    "01",
    "86",
    "a0"
  ],
  CRC: "31e8965f",
  isCritical: false,
  isPublic: false,
  isReserved: false,
  isUnsafeToCopy: false
};

const IDAT: Chunk = {
  type: "IDAT",
  length: 111,
  rawBytes: [
    "78",
    "9c",
    "ed",
    "d6",
    "31",
    "0a",
    "80",
    "30",
    "0c",
    "46",
    "e1",
    "27",
    "64",
    "68",
    "4f",
    "a1",
    "f7",
    "3f",
    "55",
    "04",
    "8f",
    "21",
    "c4",
    "dd",
    "c5",
    "45",
    "78",
    "1d",
    "52",
    "e8",
    "50",
    "28",
    "fc",
    "1f",
    "4d",
    "28",
    "d9",
    "8a",
    "01",
    "30",
    "5e",
    "7b",
    "7e",
    "9c",
    "ff",
    "ba",
    "33",
    "83",
    "1d",
    "75",
    "05",
    "47",
    "03",
    "ca",
    "06",
    "a8",
    "f9",
    "0d",
    "58",
    "a0",
    "07",
    "4e",
    "35",
    "1e",
    "22",
    "7d",
    "80",
    "5c",
    "82",
    "54",
    "e3",
    "1b",
    "b0",
    "42",
    "0f",
    "5c",
    "dc",
    "2e",
    "00",
    "79",
    "20",
    "88",
    "92",
    "ff",
    "e2",
    "a0",
    "01",
    "36",
    "a0",
    "7b",
    "40",
    "07",
    "94",
    "3c",
    "10",
    "04",
    "d9",
    "00",
    "19",
    "50",
    "36",
    "40",
    "7f",
    "01",
    "1b",
    "f0",
    "00",
    "52",
    "20",
    "1a",
    "9c"
  ],
  CRC: "160fb84c",
  isCritical: false,
  isPublic: false,
  isReserved: false,
  isUnsafeToCopy: false
};

const IEND: Chunk = {
  type: "IEND",
  length: 0,
  rawBytes: [],
  CRC: "ae426082",
  isCritical: false,
  isPublic: false,
  isReserved: false,
  isUnsafeToCopy: false
}

export const mockPNG: PNGData = {
  chunks: [IHDR, gAMA, IDAT, IDAT, IDAT, IEND],
  validPNG: true,
  id: "123456789098765432123456"
}