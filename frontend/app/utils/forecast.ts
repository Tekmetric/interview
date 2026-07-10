export function getWeatherIconUrl(iconCode: number): string {
    return `https://www.accuweather.com/assets/images/weather-icons/v2a/${iconCode}.svg`;
}

export function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString(undefined, { weekday: 'short', month: 'numeric', day: 'numeric' });
}

export function formatTime(dateString: string, includeMinutes: boolean = false): string {
    const date = new Date(dateString);
    const options: Intl.DateTimeFormatOptions = includeMinutes ? { hour: 'numeric', minute: '2-digit', hour12: true } : { hour: 'numeric', hour12: true };
    return date.toLocaleTimeString(undefined, options);
}