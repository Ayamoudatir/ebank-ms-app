import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';

interface Customer { id: number; name: string; email: string; }
interface Product { id: string; name: string; price: number; quantity: number; }
interface Bill { id: number; billingDate: string; customerId: number; productItems: { productId: string; quantity: number; unitPrice: number; }[]; }

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  template: `
    <main>
      <h1>Factures</h1>
      <p class="hint">Les appels passent par la Gateway Spring Cloud.</p>
      @if (error) { <p class="error">{{ error }}</p> }
      <section class="card">
        <h2>Créer une facture</h2>
        <label>Client
          <select [(ngModel)]="customerId">
            @for (customer of customers; track customer.id) {
              <option [ngValue]="customer.id">{{ customer.name }} — {{ customer.email }}</option>
            }
          </select>
        </label>
        <label>Produit
          <select [(ngModel)]="productId">
            @for (product of products; track product.id) {
              <option [ngValue]="product.id">{{ product.name }} ({{ product.price }} MAD)</option>
            }
          </select>
        </label>
        <label>Quantité <input type="number" min="1" [(ngModel)]="quantity"></label>
        <button (click)="createBill()" [disabled]="!customerId || !productId">Créer et afficher</button>
      </section>
      @if (bill) {
        <section class="card">
          <h2>Facture #{{ bill.id }}</h2>
          <p>Client : {{ bill.customer?.name }} ({{ bill.customer?.email }})</p>
          @for (item of bill.productItems; track item.id) {
            <p>{{ item.product?.name }} — {{ item.quantity }} × {{ item.unitPrice }} MAD</p>
          }
        </section>
      }
      <section class="card">
        <h2>Produits disponibles</h2>
        @for (product of products; track product.id) {
          <p>{{ product.name }} · {{ product.price }} MAD · stock {{ product.quantity }}</p>
        }
      </section>
    </main>
  `,
  styles: [`
    :host { font-family: Arial, sans-serif; color: #172033; }
    main { max-width: 780px; margin: 40px auto; padding: 0 20px; }
    .hint { color: #5b6475; }
    .card { border: 1px solid #dbe1ea; border-radius: 12px; padding: 20px; margin: 18px 0; }
    label { display: grid; gap: 7px; margin: 14px 0; }
    input, select, button { padding: 10px; border: 1px solid #bbc5d3; border-radius: 7px; }
    button { background: #174ea6; color: white; cursor: pointer; }
    button:disabled { opacity: .5; cursor: default; }
    .error { color: #b42318; }
  `]
})
class AppComponent implements OnInit {
  private readonly http = inject(HttpClient);
  customers: Customer[] = [];
  products: Product[] = [];
  customerId: number | null = null;
  productId: string | null = null;
  quantity = 1;
  bill: (Bill & { customer?: Customer; productItems: (Bill['productItems'][number] & { product?: Product; id?: number })[] }) | null = null;
  error = '';

  ngOnInit(): void {
    this.http.get<Customer[]>('/gateway/customers').subscribe({
      next: customers => { this.customers = customers; this.customerId = customers[0]?.id ?? null; },
      error: () => this.error = 'Impossible de charger les clients. Vérifie que customer-service et Gateway sont démarrés.'
    });
    this.http.get<Product[]>('/gateway/products').subscribe({
      next: products => { this.products = products; this.productId = products[0]?.id ?? null; },
      error: () => this.error = 'Impossible de charger les produits. Vérifie que inventory-service et Gateway sont démarrés.'
    });
  }

  createBill(): void {
    if (this.customerId === null || this.productId === null) return;
    this.error = '';
    this.http.post<Bill>('/gateway/bills', {
      customerId: this.customerId,
      productItems: [{ productId: this.productId, quantity: this.quantity }]
    }).subscribe({
      next: created => this.http.get<typeof this.bill>(`/gateway/bills/${created.id}`).subscribe({
        next: loaded => this.bill = loaded,
        error: () => this.error = 'La facture est créée, mais son affichage détaillé a échoué.'
      }),
      error: () => this.error = 'La création de la facture a échoué. Vérifie les services et les identifiants.'
    });
  }
}

bootstrapApplication(AppComponent, { providers: [provideHttpClient()] }).catch(error => console.error(error));
