import { Component, OnInit } from '@angular/core';
import { PartnerService } from '../partner/partner.service';
import { Partner } from '../partner/partner.model';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { CommonModule } from '@angular/common'; // Import CommonModule for *ngFor

@Component({
  selector: 'app-partner',
  standalone: true, // Mark this component as standalone
  templateUrl: './partner.component.html',
  styleUrls: ['./partner.component.css'],
  imports: [FormsModule, CommonModule] // Include both FormsModule and CommonModule
})
export class PartnerComponent implements OnInit {
  partners: Partner[] = []; // Array to hold partner data
  newPartner: Partner = { name: '', type: '', email: '', phone: '' }; // New partner object
  selectedPartner: Partner | null = null; // Selected partner for editing

  constructor(private partnerService: PartnerService) {}

  ngOnInit(): void {
    this.loadPartners(); // Load partners on component initialization
  }

  // Method to load all partners
  loadPartners(): void {
    this.partnerService.getAllPartners().subscribe({
      next: (data) => {
        this.partners = data; // Populate the partners array
      },
      error: (err) => console.error('Failed to load partners:', err)
    });
  }

  // Method to add a new partner
  addPartner(): void {
    this.partnerService.createPartner(this.newPartner).subscribe({
      next: () => {
        this.loadPartners(); // Reload partners after adding
        this.newPartner = { name: '', type: '', email: '', phone: '' }; // Reset new partner form
      },
      error: (err) => console.error('Failed to add partner:', err)
    });
  }

  // Method to edit an existing partner
  editPartner(partner: Partner): void {
    this.selectedPartner = { ...partner }; // Clone the partner object for editing
  }

  // Method to update a selected partner
  updatePartner(): void {
    if (!this.selectedPartner) {
      console.error('No partner selected to update');
      return; // Exit if no partner is selected
    }

    this.partnerService.updatePartner(this.selectedPartner.id!, this.selectedPartner).subscribe({
      next: () => {
        this.loadPartners(); // Reload partners after updating
        this.selectedPartner = null; // Reset selected partner after update
      },
      error: (err) => console.error('Failed to update partner:', err)
    });
  }

  // Method to delete a partner by ID
  deletePartner(id: number): void {
    this.partnerService.deletePartner(id).subscribe({
      next: () => {
        this.loadPartners(); // Reload partners after deletion
      },
      error: (err) => console.error('Failed to delete partner:', err)
    });
  }
}
