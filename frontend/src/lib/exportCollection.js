const HEADERS = [
  'Title',
  'Artist',
  'Date',
  'Medium',
  'Department',
  'Culture',
  'Public domain',
  'Met URL',
  'Image URL',
];

// Quote per RFC 4180 when a cell contains a comma, quote, or newline.
function csvCell(value) {
  const s = value == null ? '' : String(value);
  return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s;
}

export function collectionToCsv(items) {
  const rows = items.map((a) => [
    a.title ?? '',
    a.artist ?? '',
    a.date ?? '',
    a.medium ?? '',
    a.department ?? '',
    a.culture ?? '',
    a.isPublicDomain ? 'Yes' : 'No',
    a.url ?? '',
    a.image ?? '',
  ]);
  return [HEADERS, ...rows]
    .map((row) => row.map(csvCell).join(','))
    .join('\n');
}

export function downloadCollection(items) {
  // Leading BOM so spreadsheets detect UTF-8 (accents, CJK titles).
  const blob = new Blob(['﻿' + collectionToCsv(items)], {
    type: 'text/csv;charset=utf-8',
  });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'meet-the-met-collection.csv';
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}
