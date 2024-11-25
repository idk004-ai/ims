package com.group1.interview_management.services;

import com.group1.interview_management.dto.Offer.OfferExportDTO;
import org.springframework.data.domain.Page;

import com.group1.interview_management.dto.OfferCreateDTO;
import com.group1.interview_management.dto.OfferDTO;
import com.group1.interview_management.dto.OfferSearchDTO;
import com.group1.interview_management.dto.Offer.OfferDetailDTO;


import java.time.LocalDate;
import java.util.List;
public interface OfferService {
    OfferDetailDTO getOfferById(Integer id);

    OfferCreateDTO getOfferCreateDTOById(Integer id);

    void approveOffer(Integer id);

    void rejectOffer(Integer id);

    void saveOffer(OfferCreateDTO offerCreateDTO);

    Page<OfferDTO> searchOffers(OfferSearchDTO request);

    void sentOffer(Integer id);

    void acceptOffer(Integer id);

    void declinedOffer(Integer id);

    void cancelOffer(Integer id);

    List<OfferExportDTO> getOfferByDueDate(LocalDate periodFrom, LocalDate periodTo);
}
