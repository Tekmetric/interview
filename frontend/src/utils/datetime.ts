export function convertToLocaleDatetime(isoDatetime: string) {
  if (isoDatetime.length === 0) {
    return isoDatetime;
  }
  const date = new Date(isoDatetime);

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");

  // Format the date to the desired string
  const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;

  return localDateTime;
}
