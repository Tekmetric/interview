import { http, HttpResponse } from 'msw';
import { getPredictionsMockResponse } from './data/wmata';

export const handlers = [
  http.get('https://api.wmata.com/StationPrediction.svc/json/GetPrediction/:stationCodes', ({ params }) => {
    const stationCodes = params.stationCodes === 'All' ? params.stationCodes : (params.stationCodes as string | undefined)?.split(',') ?? 'All';

    // Respond with a "200 OK" response and the deleted post.
    return HttpResponse.json(stationCodes === 'All' ? getPredictionsMockResponse : { Trains: getPredictionsMockResponse.Trains.filter(t => stationCodes.includes(t.LocationCode)) });
  })
];
