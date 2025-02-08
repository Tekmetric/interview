export const RocketIconSVG = `
<svg
  xmlns="http://www.w3.org/2000/svg"
  viewBox="0 0 72 72"
  fill="none"
  stroke="black"
  stroke-width="6"
  stroke-linecap="round"
  stroke-linejoin="round"
  class="w-18 h-18"
>
  <!-- Rocket Body -->
  <path d="M36 12 
           L30 24 
           L30 40 
           L36 48 
           L42 40 
           L42 24 
           Z" />
  
  <!-- Left Fin -->
  <path d="M30 40 
           L20 52 
           L30 52 
           Z" />
  
  <!-- Right Fin -->
  <path d="M42 40 
           L42 52 
           L52 52 
           Z" />
  
  <!-- Window -->
  <circle cx="36" cy="28" r="4" />
  
  <!-- Flame at the bottom -->
  <path d="M36 48 
           L34 52 
           Q36 58 38 52 
           L36 48 
           Z" />
</svg>
`

const RocketIcon: React.FC = () => {
  return <div dangerouslySetInnerHTML={{ __html: RocketIconSVG }} />
}

export default RocketIcon
