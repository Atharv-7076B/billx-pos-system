// ─── Auth ────────────────────────────────────────────────────────────────────
export interface UserDto {
  id: number
  fullName: string
  email: string
  role: UserRole
  branchId?: number
  storeId?: number
  phoneNumber?: string
  createdAt?: string
  updatedAt?: string
  lastLogin?: string
}

export type UserRole =
  | 'ROLE_USER'
  | 'ROLE_ADMIN'
  | 'ROLE_STORE_ADMIN'
  | 'ROLE_BRANCH_CASHIER'
  | 'ROLE_BRANCH_MANAGER'
  | 'ROLE_STORE_MANAGER'

export interface AuthResponse {
  jwt: string
  message: string
  userDto: UserDto
}

export interface LoginRequest {
  email: string
  password: string
}

// ─── Store ────────────────────────────────────────────────────────────────────
export interface StoreDto {
  id?: number
  brand: string
  description?: string
  storeType?: string
  status?: StoreStatus
  storeContact?: { address?: string; phoneNo?: string; email?: string }
  storeAdmin?: UserDto
  createdAt?: string
  updatedAt?: string
}

export type StoreStatus = 'ACTIVE' | 'PENDING' | 'BLOCKED'

// ─── Branch ──────────────────────────────────────────────────────────────────
export interface BranchDto {
  id?: number
  name: string
  address?: string
  phone?: string
  email?: string
  storeId: number
  workingDays?: string[]
  openTime?: string
  closeTime?: string
  manager?: UserDto
}

// ─── Category ────────────────────────────────────────────────────────────────
export interface CategoryDto {
  id?: number
  name: string
  storeId: number
}

// ─── Product ─────────────────────────────────────────────────────────────────
export interface ProductDto {
  id?: number
  name: string
  sku: string
  description?: string
  mrp: number
  sellingPrice: number
  brand?: string
  image?: string
  category?: CategoryDto
  categoryId?: number
  storeId?: number
  createdAt?: string
  updatedAt?: string
}

// ─── Inventory ───────────────────────────────────────────────────────────────
export interface InventoryDto {
  id?: number
  branchId: number
  productId: number
  branch?: BranchDto
  product?: ProductDto
  quantity: number
  lastUpdate?: string
}

// ─── Customer ────────────────────────────────────────────────────────────────
export interface CustomerDto {
  id?: number
  name: string
  email?: string
  phone?: string
  address?: string
  storeId: number
  totalPurchases?: number
  totalOrders?: number
  createdAt?: string
  updatedAt?: string
}

// ─── Order / Billing ─────────────────────────────────────────────────────────
export interface OrderItemDto {
  id?: number
  productId: number
  productName?: string
  productSku?: string
  quantity: number
  unitPrice: number
  discountAmount: number
  total: number
}

export interface OrderDto {
  id?: number
  orderNumber?: string
  customer?: CustomerDto
  customerId?: number
  branchId?: number
  branchName?: string
  storeId?: number
  cashierId?: number
  cashierName?: string
  items?: OrderItemDto[]
  subtotal: number
  discountAmount: number
  taxPercent: number
  taxAmount: number
  total: number
  notes?: string
  paymentMethod: PaymentMethod
  paymentStatus?: PaymentStatus
  orderStatus?: OrderStatus
  razorpayOrderId?: string
  razorpayPaymentId?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateOrderRequest {
  storeId: number
  branchId?: number
  customerId?: number
  items: OrderItemDto[]
  discountAmount: number
  taxPercent: number
  paymentMethod: PaymentMethod
  notes?: string
  deductInventory: boolean
}

export type PaymentMethod = 'CASH' | 'CARD' | 'UPI' | 'RAZORPAY' | 'SPLIT'
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED'
export type OrderStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED'

// ─── Dashboard ───────────────────────────────────────────────────────────────
export interface DashboardStatsDto {
  totalRevenue: number
  todaySales: number
  monthlySales: number
  totalOrders: number
  todayOrders: number
  totalProducts: number
  totalCustomers: number
  totalEmployees: number
  lowStockItems: number
  recentOrders: RecentOrder[]
  salesChartData: ChartPoint[]
  topProducts: TopProduct[]
  lowStockAlerts: LowStockAlert[]
}

export interface RecentOrder {
  id: number
  orderNumber: string
  total: number
  status: OrderStatus
  paymentMethod: PaymentMethod
  customerName: string
  createdAt: string
}

export interface ChartPoint {
  date: string
  sales: number
}

export interface TopProduct {
  productId: number
  productName: string
  totalQuantity: number
  totalRevenue: number
}

export interface LowStockAlert {
  productId: number
  productName: string
  sku: string
  quantity: number
  branchName: string
}

// ─── API Response ─────────────────────────────────────────────────────────────
export interface StandardResponse<T> {
  success: boolean
  message?: string
  data: T
  errors?: Record<string, string>
}

export interface ApiResponse {
  message: string
}

// ─── Cart (frontend state) ────────────────────────────────────────────────────
export interface CartItem {
  product: ProductDto
  quantity: number
  unitPrice: number
  discountAmount: number
  total: number
}
