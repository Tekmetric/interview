// The `medium` field is free text and mostly unique, so only the handful of
// values that recur constantly are translated; everything else falls back to
// English. Keyed by the lowercased English medium.
const MEDIUM_TRANSLATIONS = {
  'oil on canvas': { es: 'Óleo sobre lienzo', fr: 'Huile sur toile', pt: 'Óleo sobre tela', ja: 'キャンバスに油彩' },
  'oil on wood': { es: 'Óleo sobre tabla', fr: 'Huile sur bois', pt: 'Óleo sobre madeira', ja: '板に油彩' },
  'oil on panel': { es: 'Óleo sobre tabla', fr: 'Huile sur panneau', pt: 'Óleo sobre painel', ja: 'パネルに油彩' },
  'tempera on wood': { es: 'Temple sobre tabla', fr: 'Tempera sur bois', pt: 'Têmpera sobre madeira', ja: '板にテンペラ' },
  'tempera and gold on wood': { es: 'Temple y oro sobre tabla', fr: 'Tempera et or sur bois', pt: 'Têmpera e ouro sobre madeira', ja: '板にテンペラと金' },
  watercolor: { es: 'Acuarela', fr: 'Aquarelle', pt: 'Aquarela', ja: '水彩' },
  gouache: { es: 'Gouache', fr: 'Gouache', pt: 'Guache', ja: 'グアッシュ' },
  bronze: { es: 'Bronce', fr: 'Bronze', pt: 'Bronze', ja: 'ブロンズ' },
  'copper alloy': { es: 'Aleación de cobre', fr: 'Alliage de cuivre', pt: 'Liga de cobre', ja: '銅合金' },
  marble: { es: 'Mármol', fr: 'Marbre', pt: 'Mármore', ja: '大理石' },
  limestone: { es: 'Piedra caliza', fr: 'Calcaire', pt: 'Calcário', ja: '石灰岩' },
  sandstone: { es: 'Arenisca', fr: 'Grès', pt: 'Arenito', ja: '砂岩' },
  stone: { es: 'Piedra', fr: 'Pierre', pt: 'Pedra', ja: '石' },
  terracotta: { es: 'Terracota', fr: 'Terre cuite', pt: 'Terracota', ja: 'テラコッタ' },
  ceramic: { es: 'Cerámica', fr: 'Céramique', pt: 'Cerâmica', ja: '陶磁器' },
  porcelain: { es: 'Porcelana', fr: 'Porcelaine', pt: 'Porcelana', ja: '磁器' },
  earthenware: { es: 'Loza', fr: 'Faïence', pt: 'Faiança', ja: '陶器' },
  stoneware: { es: 'Gres', fr: 'Grès', pt: 'Grés', ja: '炻器' },
  gold: { es: 'Oro', fr: 'Or', pt: 'Ouro', ja: '金' },
  silver: { es: 'Plata', fr: 'Argent', pt: 'Prata', ja: '銀' },
  wood: { es: 'Madera', fr: 'Bois', pt: 'Madeira', ja: '木' },
  ivory: { es: 'Marfil', fr: 'Ivoire', pt: 'Marfim', ja: '象牙' },
  glass: { es: 'Vidrio', fr: 'Verre', pt: 'Vidro', ja: 'ガラス' },
  jade: { es: 'Jade', fr: 'Jade', pt: 'Jade', ja: '翡翠' },
  silk: { es: 'Seda', fr: 'Soie', pt: 'Seda', ja: '絹' },
  cotton: { es: 'Algodón', fr: 'Coton', pt: 'Algodão', ja: '綿' },
  wool: { es: 'Lana', fr: 'Laine', pt: 'Lã', ja: '羊毛' },
  linen: { es: 'Lino', fr: 'Lin', pt: 'Linho', ja: '亜麻' },
  leather: { es: 'Cuero', fr: 'Cuir', pt: 'Couro', ja: '革' },
  paper: { es: 'Papel', fr: 'Papier', pt: 'Papel', ja: '紙' },
  etching: { es: 'Aguafuerte', fr: 'Eau-forte', pt: 'Água-forte', ja: 'エッチング' },
  engraving: { es: 'Grabado', fr: 'Gravure', pt: 'Gravura', ja: 'エングレービング' },
  woodcut: { es: 'Xilografía', fr: 'Gravure sur bois', pt: 'Xilogravura', ja: '木版画' },
  lithograph: { es: 'Litografía', fr: 'Lithographie', pt: 'Litografia', ja: 'リトグラフ' },
  'albumen silver print': { es: 'Copia a la albúmina', fr: 'Épreuve sur papier albuminé', pt: 'Impressão em papel albuminado', ja: '鶏卵紙プリント' },
  'gelatin silver print': { es: 'Copia en gelatina de plata', fr: 'Épreuve gélatino-argentique', pt: 'Impressão em gelatina e prata', ja: 'ゼラチンシルバープリント' },
};

export function translateMedium(medium, locale) {
  if (!medium || locale === 'en') return medium;
  const entry = MEDIUM_TRANSLATIONS[medium.trim().toLowerCase()];
  return entry?.[locale] ?? medium;
}
