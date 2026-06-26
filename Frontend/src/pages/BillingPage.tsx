import { useState, useMemo, useCallback } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import {
  Search, Plus, Minus, Trash2, ShoppingCart, Receipt, X, Package,
  CreditCard, Banknote, Smartphone, CheckCircle2, Printer, RefreshCw, Users,
  Loader2
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { productService } from '../services/productService'
import { customerService } from '../services/customerService'
import { orderService } from '../services/orderService'
import { CartItem, CustomerDto, PaymentMethod, ProductDto } from '../types'
import { formatCurrency } from '../utils/formatters'
import clsx from 'clsx'

const PAYMENT_METHODS: { id: PaymentMethod; label: string; icon: typeof Banknote }[] = [
  { id: 'CASH',    label: 'Cash',    icon: Banknote },
  { id: 'CARD',    label: 'Card',    icon: CreditCard },
  { id: 'UPI',     label: 'UPI',     icon: Smartphone },
]

export default function BillingPage() {
  const { storeId, user } = useAuth()
  const qc = useQueryClient()

  const [search, setSearch]               = useState('')
  const [cart, setCart]                   = useState<CartItem[]>([])
  const [discount, setDiscount]           = useState(0)
  const [tax, setTax]                     = useState(0)
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CASH')
  const [selectedCustomer, setSelectedCustomer] = useState<CustomerDto | null>(null)
  const [customerSearch, setCustomerSearch]     = useState('')
  const [showCustomerPanel, setShowCustomerPanel] = useState(false)
  const [showReceipt, setShowReceipt]     = useState(false)
  const [lastOrder, setLastOrder]         = useState<any>(null)

  const { data: products = [] } = useQuery({
    queryKey: ['products', storeId], queryFn: () => productService.getByStore(storeId!), enabled: !!storeId,
  })
  const { data: customers = [] } = useQuery({
    queryKey: ['customers', storeId], queryFn: () => customerService.getByStore(storeId!), enabled: !!storeId,
  })

  const filtered = useMemo(() => {
    if (!search.trim()) return products
    const q = search.toLowerCase()
    return products.filter(p => p.name.toLowerCase().includes(q) || p.sku.toLowerCase().includes(q) || p.brand?.toLowerCase().includes(q))
  }, [products, search])

  const filteredCustomers = useMemo(() => {
    if (!customerSearch.trim()) return customers.slice(0, 8)
    const q = customerSearch.toLowerCase()
    return customers.filter(c => c.name.toLowerCase().includes(q) || c.phone?.includes(q)).slice(0, 8)
  }, [customers, customerSearch])

  const addToCart = useCallback((product: ProductDto) => {
    setCart(prev => {
      const existing = prev.find(i => i.product.id === product.id)
      if (existing) {
        return prev.map(i => i.product.id === product.id
          ? { ...i, quantity: i.quantity + 1, total: (i.quantity + 1) * i.unitPrice - i.discountAmount }
          : i
        )
      }
      return [...prev, { product, quantity: 1, unitPrice: product.sellingPrice, discountAmount: 0, total: product.sellingPrice }]
    })
    toast.success(`${product.name} added`, { duration: 800, icon: '🛒' })
  }, [])

  const updateQty = (productId: number, delta: number) => {
    setCart(prev => prev.map(i => {
      if (i.product.id !== productId) return i
      const qty = Math.max(1, i.quantity + delta)
      return { ...i, quantity: qty, total: qty * i.unitPrice - i.discountAmount }
    }))
  }
  const updateItemDiscount = (productId: number, disc: number) => {
    setCart(prev => prev.map(i => {
      if (i.product.id !== productId) return i
      const d = Math.max(0, Math.min(disc, i.quantity * i.unitPrice))
      return { ...i, discountAmount: d, total: i.quantity * i.unitPrice - d }
    }))
  }
  const removeItem = (productId: number) => setCart(prev => prev.filter(i => i.product.id !== productId))
  const clearCart = () => { setCart([]); setDiscount(0); setTax(0); setSelectedCustomer(null) }

  const subtotal = cart.reduce((s, i) => s + i.total, 0)
  const taxAmount = subtotal * (tax / 100)
  const total = Math.max(0, subtotal - discount + taxAmount)

  const checkoutMut = useMutation({
    mutationFn: () => orderService.create({
      storeId: storeId!,
      customerId: selectedCustomer?.id,
      items: cart.map(i => ({ productId: i.product.id!, quantity: i.quantity, unitPrice: i.unitPrice, discountAmount: i.discountAmount, total: i.total })),
      discountAmount: discount,
      taxPercent: tax,
      paymentMethod,
      deductInventory: true,
    }),
    onSuccess: (order) => {
      qc.invalidateQueries({ queryKey: ['orders'] })
      qc.invalidateQueries({ queryKey: ['dashboard'] })
      setLastOrder(order)
      setShowReceipt(true)
      clearCart()
      toast.success('Order completed! 🎉')
    },
    onError: (e: any) => toast.error(e.response?.data?.message ?? 'Checkout failed'),
  })

  return (
    <div className="flex h-[calc(100vh-130px)] gap-4 -mt-1">
      {/* ── Left: Product Panel ── */}
      <div className="flex-1 flex flex-col min-w-0 bg-white rounded-2xl border border-gray-100 shadow-card overflow-hidden">
        {/* Search bar */}
        <div className="p-4 border-b border-gray-100">
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              className="input pl-9 bg-gray-50 border-gray-100"
              placeholder="Search products by name, SKU, or brand…"
              value={search}
              onChange={e => setSearch(e.target.value)}
              autoFocus
            />
            {search && <button onClick={() => setSearch('')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"><X size={14}/></button>}
          </div>
        </div>

        {/* Product grid */}
        <div className="flex-1 overflow-y-auto p-4">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-gray-300">
              <Package size={48} className="mb-3" />
              <p className="text-sm">No products found</p>
            </div>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-3 xl:grid-cols-4 gap-3">
              {filtered.map(product => (
                <button
                  key={product.id}
                  onClick={() => addToCart(product)}
                  className="text-left p-3 rounded-xl border border-gray-100 hover:border-primary-200 hover:bg-primary-50/30 active:scale-[0.98] transition-all duration-100 group"
                >
                  <div className="w-full aspect-square bg-gray-50 rounded-lg mb-2.5 flex items-center justify-center group-hover:bg-primary-50 transition-colors">
                    <Package size={28} className="text-gray-300 group-hover:text-primary-400 transition-colors" />
                  </div>
                  <p className="text-xs font-semibold text-gray-800 leading-snug truncate">{product.name}</p>
                  <p className="text-[10px] text-gray-400 truncate mt-0.5">{product.sku}</p>
                  <p className="text-sm font-bold text-primary-600 mt-1">{formatCurrency(product.sellingPrice)}</p>
                  {product.category && <span className="text-[10px] badge-blue mt-1">{product.category.name}</span>}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* ── Right: Cart ── */}
      <div className="w-[380px] shrink-0 flex flex-col bg-white rounded-2xl border border-gray-100 shadow-card overflow-hidden">
        {/* Cart header */}
        <div className="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ShoppingCart size={18} className="text-primary-600" />
            <span className="font-semibold text-gray-800">Cart</span>
            {cart.length > 0 && <span className="w-5 h-5 bg-primary-500 text-white text-xs rounded-full flex items-center justify-center font-bold">{cart.reduce((s,i)=>s+i.quantity,0)}</span>}
          </div>
          <div className="flex gap-1">
            <button onClick={() => setShowCustomerPanel(v => !v)} title="Select customer"
              className={clsx('p-1.5 rounded-lg transition-colors', showCustomerPanel ? 'bg-primary-100 text-primary-600' : 'text-gray-400 hover:bg-gray-100')}>
              <Users size={16}/>
            </button>
            {cart.length > 0 && (
              <button onClick={clearCart} className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                <RefreshCw size={16}/>
              </button>
            )}
          </div>
        </div>

        {/* Customer selector panel */}
        {showCustomerPanel && (
          <div className="border-b border-gray-100 p-3 bg-gray-50">
            {selectedCustomer ? (
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-full bg-primary-100 text-primary-700 font-bold text-sm flex items-center justify-center">{selectedCustomer.name[0]}</div>
                  <div><p className="text-xs font-semibold text-gray-800">{selectedCustomer.name}</p><p className="text-[10px] text-gray-500">{selectedCustomer.phone ?? selectedCustomer.email ?? ''}</p></div>
                </div>
                <button onClick={() => setSelectedCustomer(null)} className="text-gray-400 hover:text-red-500"><X size={14}/></button>
              </div>
            ) : (
              <>
                <input className="input text-xs mb-2" placeholder="Search customer by name or phone…" value={customerSearch} onChange={e => setCustomerSearch(e.target.value)} />
                <div className="max-h-32 overflow-y-auto space-y-1">
                  {filteredCustomers.map(c => (
                    <button key={c.id} onClick={() => { setSelectedCustomer(c); setShowCustomerPanel(false) }}
                      className="w-full text-left px-2 py-1.5 rounded-lg hover:bg-primary-50 text-xs flex items-center gap-2">
                      <div className="w-6 h-6 rounded-full bg-primary-100 text-primary-700 font-bold text-[10px] flex items-center justify-center shrink-0">{c.name[0]}</div>
                      <span className="truncate">{c.name} {c.phone ? `· ${c.phone}` : ''}</span>
                    </button>
                  ))}
                  {filteredCustomers.length === 0 && <p className="text-xs text-gray-400 py-1 text-center">No customers found</p>}
                </div>
              </>
            )}
          </div>
        )}

        {/* Cart items */}
        <div className="flex-1 overflow-y-auto">
          {cart.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-gray-300 py-12">
              <ShoppingCart size={40} className="mb-3" />
              <p className="text-sm">Cart is empty</p>
              <p className="text-xs mt-1">Click products to add them</p>
            </div>
          ) : (
            <div className="p-3 space-y-2">
              {cart.map(item => (
                <div key={item.product.id} className="bg-gray-50 rounded-xl p-3">
                  <div className="flex items-start justify-between gap-2">
                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-semibold text-gray-800 truncate">{item.product.name}</p>
                      <p className="text-[10px] text-gray-400">{formatCurrency(item.unitPrice)} each</p>
                    </div>
                    <button onClick={() => removeItem(item.product.id!)} className="text-gray-300 hover:text-red-500 transition-colors shrink-0"><Trash2 size={13}/></button>
                  </div>
                  <div className="flex items-center justify-between mt-2">
                    <div className="flex items-center gap-1">
                      <button onClick={() => updateQty(item.product.id!, -1)} className="w-6 h-6 rounded-lg bg-white border flex items-center justify-center hover:border-primary-300 transition-colors"><Minus size={11}/></button>
                      <span className="w-7 text-center text-sm font-bold">{item.quantity}</span>
                      <button onClick={() => updateQty(item.product.id!, +1)} className="w-6 h-6 rounded-lg bg-white border flex items-center justify-center hover:border-primary-300 transition-colors"><Plus size={11}/></button>
                    </div>
                    <span className="text-sm font-bold text-gray-900">{formatCurrency(item.total)}</span>
                  </div>
                  {/* Item discount */}
                  <div className="mt-2 flex items-center gap-2">
                    <span className="text-[10px] text-gray-400">Item disc. ₹</span>
                    <input type="number" min={0} value={item.discountAmount || ''} onChange={e => updateItemDiscount(item.product.id!, parseFloat(e.target.value) || 0)}
                      className="w-16 text-xs px-1.5 py-0.5 border border-gray-200 rounded-md focus:outline-none focus:border-primary-400" placeholder="0" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Totals & Payment */}
        {cart.length > 0 && (
          <div className="border-t border-gray-100 p-4 space-y-3">
            {/* Discount & Tax */}
            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="text-[10px] text-gray-500 font-medium uppercase tracking-wide">Discount (₹)</label>
                <input type="number" min={0} value={discount || ''} onChange={e => setDiscount(parseFloat(e.target.value)||0)}
                  className="input mt-1 text-sm py-1.5" placeholder="0.00" />
              </div>
              <div>
                <label className="text-[10px] text-gray-500 font-medium uppercase tracking-wide">Tax (%)</label>
                <input type="number" min={0} max={100} value={tax || ''} onChange={e => setTax(parseFloat(e.target.value)||0)}
                  className="input mt-1 text-sm py-1.5" placeholder="0" />
              </div>
            </div>

            {/* Summary */}
            <div className="bg-gray-50 rounded-xl p-3 space-y-1.5 text-xs">
              <div className="flex justify-between text-gray-500"><span>Subtotal</span><span>{formatCurrency(subtotal)}</span></div>
              {discount > 0 && <div className="flex justify-between text-red-500"><span>Discount</span><span>-{formatCurrency(discount)}</span></div>}
              {tax > 0 && <div className="flex justify-between text-gray-500"><span>Tax ({tax}%)</span><span>{formatCurrency(taxAmount)}</span></div>}
              <div className="flex justify-between font-bold text-base text-gray-900 border-t border-gray-200 pt-2 mt-1">
                <span>Total</span><span className="text-primary-600">{formatCurrency(total)}</span>
              </div>
            </div>

            {/* Payment method */}
            <div>
              <p className="text-[10px] text-gray-500 font-medium uppercase tracking-wide mb-1.5">Payment Method</p>
              <div className="grid grid-cols-3 gap-1.5">
                {PAYMENT_METHODS.map(({ id, label, icon: Icon }) => (
                  <button key={id} onClick={() => setPaymentMethod(id)}
                    className={clsx('flex flex-col items-center gap-1 py-2 rounded-lg border text-xs font-medium transition-all',
                      paymentMethod === id ? 'border-primary-500 bg-primary-50 text-primary-700' : 'border-gray-200 hover:border-gray-300 text-gray-600')}>
                    <Icon size={16}/>{label}
                  </button>
                ))}
              </div>
            </div>

            {/* Customer indicator */}
            {selectedCustomer && (
              <p className="text-xs text-primary-600 font-medium flex items-center gap-1.5">
                <Users size={12}/> Billing to: {selectedCustomer.name}
              </p>
            )}

            {/* Checkout */}
            <button
              onClick={() => checkoutMut.mutate()}
              disabled={checkoutMut.isPending || cart.length === 0}
              className="btn-primary w-full justify-center py-3 text-base"
            >
              {checkoutMut.isPending
                ? <><Loader2 size={18} className="animate-spin"/> Processing…</>
                : <><CheckCircle2 size={18}/> Complete Sale • {formatCurrency(total)}</>
              }
            </button>
          </div>
        )}
      </div>

      {/* ── Receipt Modal ── */}
      {showReceipt && lastOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={() => setShowReceipt(false)} />
          <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6">
            <div className="flex flex-col items-center text-center mb-6">
              <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mb-3">
                <CheckCircle2 size={36} className="text-emerald-500" />
              </div>
              <h3 className="text-xl font-bold text-gray-900">Payment Successful!</h3>
              <p className="text-gray-500 text-sm mt-1">{lastOrder.orderNumber}</p>
            </div>
            <div className="bg-gray-50 rounded-xl p-4 mb-4 space-y-1.5 text-sm">
              <div className="flex justify-between"><span className="text-gray-500">Subtotal</span><span>{formatCurrency(lastOrder.subtotal)}</span></div>
              {lastOrder.discountAmount > 0 && <div className="flex justify-between text-red-500"><span>Discount</span><span>-{formatCurrency(lastOrder.discountAmount)}</span></div>}
              {lastOrder.taxAmount > 0 && <div className="flex justify-between text-gray-500"><span>Tax</span><span>{formatCurrency(lastOrder.taxAmount)}</span></div>}
              <div className="flex justify-between font-bold text-base border-t border-gray-200 pt-2 mt-1">
                <span>Total Paid</span><span className="text-primary-600">{formatCurrency(lastOrder.total)}</span>
              </div>
            </div>
            <div className="flex gap-3">
              <button onClick={() => setShowReceipt(false)} className="btn-secondary flex-1">Close</button>
              <button onClick={() => window.print()} className="btn-primary flex-1 justify-center">
                <Printer size={15}/> Print
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
