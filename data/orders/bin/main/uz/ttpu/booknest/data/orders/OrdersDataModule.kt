package uz.ttpu.booknest.data.orders

object OrdersDataModule {
    fun provideRepository(): OrderRepository = FakeOrderRepository()
}
