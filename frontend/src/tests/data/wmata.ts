export const A01Train = {
  Car: '6',
  Destination: 'SilvrSpg',
  DestinationCode: 'B08',
  DestinationName: 'Silver Spring',
  Group: '1',
  Line: 'RD',
  LocationCode: 'A01',
  LocationName: 'Metro Center',
  Min: '3'
};

export const A02Train = {
  Car: '6',
  Destination: 'SilvrSpg',
  DestinationCode: 'B08',
  DestinationName: 'Silver Spring',
  Group: '1',
  Line: 'RD',
  LocationCode: 'A02',
  LocationName: 'Farragut North',
  Min: '3'
};

export const trainsByCode = {
  A01: A01Train,
  A02: A02Train
};

export const getPredictionsMockResponse = {
  Trains: [A01Train, A02Train, {
    Car: '6',
    Destination: 'Shady Gr',
    DestinationCode: 'A15',
    DestinationName: 'Shady Grove',
    Group: '2',
    Line: 'RD',
    LocationCode: 'A03',
    LocationName: 'Dupont Circle',
    Min: '6'
  }, {
    Car: '8',
    Destination: 'Glenmont',
    DestinationCode: 'B11',
    DestinationName: 'Glenmont',
    Group: '1',
    Line: 'RD',
    LocationCode: 'A04',
    LocationName: 'Woodley Park-Zoo/Adams Morgan',
    Min: '8'
  }, {
    Car: '6',
    Destination: 'SilvrSpg',
    DestinationCode: 'B08',
    DestinationName: 'Silver Spring',
    Group: '1',
    Line: 'RD',
    LocationCode: 'A05',
    LocationName: 'Cleveland Park',
    Min: '9'
  }, {
    Car: '6',
    Destination: 'Grsvnor',
    DestinationCode: 'A11',
    DestinationName: 'Grosvenor-Strathmore',
    Group: '2',
    Line: 'RD',
    LocationCode: 'A06',
    LocationName: 'Van Ness-UDC',
    Min: '9'
  }]
};
