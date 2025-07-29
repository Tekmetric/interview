export const Logo = () => {
  return (
    <svg
      width="180"
      height="40"
      viewBox="0 0 180 40"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
    >
      <text x="20" y="26" fontSize="18" fill="var(--accent)" fontWeight="bold">
        MOVIEBASE
      </text>
      <g transform="translate(140, 10)">
        <rect x="0" y="0" width="20" height="20" rx="3" fill="var(--accent)" />
        <polygon points="6,5 14,10 6,15" fill="#222" />
      </g>
    </svg>
  );
};
