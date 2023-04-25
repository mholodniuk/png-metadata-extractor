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

export type ChunkType =
  | 'IHDR'
  | 'PLTE'
  | 'IDAT'
  | 'IEND'
  | 'cHRM'
  | 'gAMA'
  | 'iCCP'
  | 'sBIT'
  | 'sRGB'
  | 'bKGD'
  | 'hIST'
  | 'tRNS'
  | 'pHYs'
  | 'sPLT'
  | 'tIME'
  | 'iTXt'
  | 'tEXt'
  | 'zTXt';

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

const PLTE: Chunk = {
  type: "PLTE",
  length: 66,
  rawBytes: [
      "e6",
      "e6",
      "... (mock response)",
      "bb"
  ],
  properties: {
    'Palette': [
      {
          "red": 230,
          "green": 230,
          "blue": 230
      },
      {
          "red": 93,
          "green": 134,
          "blue": 96
      },
      {
          "red": 61,
          "green": 98,
          "blue": 58
      },
      {
          "red": 81,
          "green": 119,
          "blue": 82
      },
      {
          "red": 21,
          "green": 54,
          "blue": 14
      },
      {
          "red": 34,
          "green": 67,
          "blue": 27
      },
      {
          "red": 48,
          "green": 83,
          "blue": 43
      },
      {
          "red": 255,
          "green": 255,
          "blue": 255
      },
      {
          "red": 2,
          "green": 11,
          "blue": 1
      },
      {
          "red": 10,
          "green": 34,
          "blue": 8
      },
      {
          "red": 71,
          "green": 116,
          "blue": 62
      },
      {
          "red": 105,
          "green": 152,
          "blue": 99
      },
      {
          "red": 111,
          "green": 153,
          "blue": 119
      },
      {
          "red": 38,
          "green": 88,
          "blue": 17
      },
      {
          "red": 126,
          "green": 166,
          "blue": 127
      },
      {
          "red": 57,
          "green": 115,
          "blue": 28
      },
      {
          "red": 142,
          "green": 176,
          "blue": 143
      },
      {
          "red": 242,
          "green": 240,
          "blue": 243
      },
      {
          "red": 89,
          "green": 143,
          "blue": 63
      },
      {
          "red": 167,
          "green": 185,
          "blue": 162
      },
      {
          "red": 213,
          "green": 217,
          "blue": 210
      },
      {
          "red": 191,
          "green": 201,
          "blue": 187
      }
    ],
    "Number of entries": 22
  },
  CRC: "ca353f61",
  isCritical: true,
  isPublic: true,
  isReserved: true,
  isUnsafeToCopy: true
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
  chunks: [IHDR, gAMA, PLTE, IDAT, IDAT, IDAT, IEND],
  validPNG: true,
  id: "123456789098765432123456"
}