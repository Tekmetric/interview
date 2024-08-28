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

  const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;

  return localDateTime;
}

export function formatDatetime(isoDatetime: string) {
  const formattedDate = new Intl.DateTimeFormat("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  }).format(new Date(isoDatetime));
  return formattedDate;
}

export function getCurrentDatetimeLocal() {
  return new Date().toISOString().slice(0, 16);
}
