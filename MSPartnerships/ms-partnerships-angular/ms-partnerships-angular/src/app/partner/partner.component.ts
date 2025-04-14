import { Component, OnInit } from '@angular/core';
import { PartnerService } from './partner.service';
import { Partner } from './partner.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-partner',
  standalone: true,
  templateUrl: './partner.component.html',
  styleUrls: ['./partner.component.css'],
  imports: [FormsModule, CommonModule]
})
export class PartnerComponent implements OnInit {
  partners: Partner[] = [];
  newPartner: Partner = { name: '', type: '', email: '', phone: '' };
  selectedPartner: Partner | null = null;
  showAddForm = false;
  isProcessing = false;
  errorMessage: string | null = null;

  constructor(private partnerService: PartnerService) {}

  ngOnInit(): void {
    this.loadPartners();
  }

  loadPartners(): void {
    this.partnerService.getAllPartners().subscribe({
      next: (data) => {
        this.partners = data;
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }

  addPartner(): void {
    this.partnerService.createPartner(this.newPartner).subscribe({
      next: () => {
        this.newPartner = { name: '', type: '', email: '', phone: '' };
        this.showAddForm = false;
        this.loadPartners();
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }

  editPartner(partner: Partner): void {
    this.selectedPartner = { ...partner };
  }

  updatePartner(): void {
    if (!this.selectedPartner?.id) return;

    this.partnerService.updatePartner(this.selectedPartner.id, this.selectedPartner).subscribe({
      next: () => {
        this.selectedPartner = null;
        this.loadPartners();
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }

  confirmDelete(id: number): void {
    if (confirm('Are you sure you want to delete this partner?')) {
      this.partnerService.deletePartner(id).subscribe({
        next: () => {
          this.loadPartners();
        },
        error: (err) => {
          this.errorMessage = err.message;
        }
      });
    }
  }

  cancelEdit(): void {
    this.selectedPartner = null;
  }
}
