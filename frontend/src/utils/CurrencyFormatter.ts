let USDollar = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
});

export default function currencyFormatter(money: number) {
    return USDollar.format(money);
}