import { CSSProperties } from 'react';
import { PokemonTypeName } from '../types/pokemon';

export const COLUMN_WIDTHS = {
  id: '60px',
  image: '140px',
  name: '150px',
  height: '80px',
  weight: '120px',
  stats: '250px'
} as const;

export const typeColors: Record<PokemonTypeName, string> = {
  normal: '#A8A77A',
  fire: '#FD7D24',
  water: '#6390F0',
  electric: '#F7D02C',
  grass: '#9BCC50',
  ice: '#51C4E7',
  fighting: '#C22E28',
  poison: '#B97FC9',
  ground: '#E2BF65',
  flying: '#A98FF3',
  psychic: '#F95587',
  bug: '#A6B91A',
  rock: '#B6A136',
  ghost: '#735797',
  dragon: '#6F35FC',
  dark: '#705746',
  steel: '#B7B7CE',
  fairy: '#D685AD'
};

export const backgroundStyle: CSSProperties = {
  backgroundImage: `
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.3) 10%, transparent 10%),
    radial-gradient(circle at 80% 50%, rgba(255, 255, 255, 0.3) 10%, transparent 10%),
    radial-gradient(circle at 40% 20%, rgba(0, 0, 0, 0.1) 8%, transparent 8%),
    radial-gradient(circle at 60% 80%, rgba(0, 0, 0, 0.1) 8%, transparent 8%),
    linear-gradient(135deg, #FF6B6B 0%, #C92A2A 100%)
  `,
  backgroundSize: '200px 200px, 200px 200px, 150px 150px, 150px 150px, 100% 100%',
  backgroundPosition: '0 0, 100px 100px, 50px 50px, 150px 150px, 0 0'
};

export const darkBackgroundStyle: CSSProperties = {
  backgroundImage: `
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 10%, transparent 10%),
    radial-gradient(circle at 80% 50%, rgba(255, 255, 255, 0.1) 10%, transparent 10%),
    radial-gradient(circle at 40% 20%, rgba(0, 0, 0, 0.3) 8%, transparent 8%),
    radial-gradient(circle at 60% 80%, rgba(0, 0, 0, 0.3) 8%, transparent 8%),
    linear-gradient(135deg, #1e293b 0%, #0f172a 100%)
  `,
  backgroundSize: '200px 200px, 200px 200px, 150px 150px, 150px 150px, 100% 100%',
  backgroundPosition: '0 0, 100px 100px, 50px 50px, 150px 150px, 0 0'
};

// Helper functions
const responsivePadding = (isMobile: boolean): string => isMobile ? 'px-2' : 'px-5';
const responsiveContainerPadding = (isMobile: boolean): string => isMobile ? 'px-2.5' : 'px-10';
const baseCell = (isMobile: boolean, additionalClasses: string = ''): string =>
  `flex items-center ${responsivePadding(isMobile)} box-border${additionalClasses ? ' ' + additionalClasses : ''}`;

// Class names
export const classes = {
  loadingContainer: 'flex flex-col justify-center items-center h-screen gap-5 dark:text-white',
  loadingImage: 'w-[100px] h-[100px] animate-spin',
  loadingText: 'text-lg font-medium dark:text-white',

  errorContainer: 'flex flex-col justify-center items-center h-screen gap-5 p-10 dark:text-white',
  errorIcon: 'text-6xl',
  errorTitle: 'text-2xl font-bold text-red-600 dark:text-red-400',
  errorMessage: 'text-lg text-gray-700 dark:text-gray-300 text-center max-w-md',
  errorButton: 'mt-4 px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 dark:bg-red-700 dark:hover:bg-red-800 transition-colors',

  container: (isMobile: boolean): string => `w-full min-h-screen bg-[#FF6B6B] dark:bg-slate-900 ${isMobile ? 'p-5 px-2.5' : 'p-10 px-5'} transition-colors duration-200`,
  card: 'w-full max-w-[800px] h-screen mx-auto bg-white dark:bg-slate-800 rounded-2xl shadow-2xl overflow-hidden border-4 border-gray-800 dark:border-slate-700 flex flex-col transition-colors duration-200',
  header: (isMobile: boolean): string => `bg-gradient-to-br from-[#FF6B6B] to-[#C92A2A] dark:from-slate-700 dark:to-slate-800 text-white ${isMobile ? 'p-6 px-5 pb-5' : 'p-10 pb-7'} transition-colors duration-200`,
  title: (isMobile: boolean): string => `m-0 mb-2 font-bold tracking-tight ${isMobile ? 'text-[32px]' : 'text-[42px]'}`,
  tagline: (isMobile: boolean): string => `m-0 mb-6 italic opacity-90 ${isMobile ? 'text-sm' : 'text-base'}`,
  searchInput: 'w-full py-3.5 px-5 text-base text-gray-900 dark:text-white dark:bg-slate-700 dark:placeholder-gray-400 border-none rounded-xl shadow-md outline-none transition-shadow focus:shadow-lg',

  tableHeaderContainer: responsiveContainerPadding,
  tableHeader: (isMobile: boolean): string => `flex font-semibold text-[#666] dark:text-gray-400 uppercase tracking-wider border-b-2 border-[#e0e0e0] dark:border-slate-600 pb-4 pt-6 items-center ${isMobile ? 'text-[10px]' : 'text-xs'} transition-colors duration-200`,
  tableHeaderCell: baseCell,
  tableHeaderCellImage: (isMobile: boolean): string => baseCell(isMobile, 'justify-center'),

  listContainer: (isMobile: boolean): string => `${isMobile ? 'px-2.5 pb-5' : 'px-10 pb-10'} outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400 focus:ring-inset flex-1 overflow-hidden`,

  row: 'flex border-b border-[#e0e0e0] dark:border-slate-600 hover:bg-[#f5f5f5] dark:hover:bg-slate-700 transition-colors pb-1',
  cellId: (isMobile: boolean): string => baseCell(isMobile, 'justify-center dark:text-gray-300'),
  cellImage: (isMobile: boolean): string => `flex flex-col items-center justify-center gap-1 ${responsivePadding(isMobile)} box-border`,
  cellName: baseCell,
  cellHeight: baseCell,
  cellWeight: baseCell,
  cellStats: 'flex items-center px-5 box-border text-[10px] overflow-hidden',

  pokemonImage: 'block h-[60px]',
  typeBadgeContainer: 'flex gap-1 flex-wrap justify-center',
  typeBadge: 'px-2 py-0.5 rounded-full text-white text-[10px] font-semibold uppercase',
  pokemonLink: 'text-[#3b82f6] dark:text-blue-400 no-underline hover:underline',

  footer: (isMobile: boolean): string => `text-center py-4 text-sm text-gray-500 dark:text-gray-400 border-t border-gray-200 dark:border-slate-600 ${responsiveContainerPadding(isMobile)} transition-colors duration-200`
};
