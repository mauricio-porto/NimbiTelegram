package com.braintech.nimbitelegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = { "http://localhost:4200" }, allowedHeaders = { "*" }, maxAge = 900)
@RestController
public class PurchaseOrdersController {

    private static final int DELAY_PER_ITEM_MS = 100;

//    private final NimbiComprasService nimbiComprasService;
//    private final NimbiComprasClient nimbiComprasClient;
//
//    @GetMapping("/api/purchaseOrders")
////    @RequestMapping("/api")
//    public Flux<PurchaseOrders> getPurchaseOrdersFlux() {
//        return nimbiComprasService.findAllPurchaseOrders();
//    }
//
//    @GetMapping("/api/purchaseOrdersDTO")
////    @RequestMapping("/api")
//    public Flux<PurchaseOrderDTO> getPurchaseOrdersDTOFlux() {
//        return nimbiComprasService.findAllPurchaseOrdersDTO().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
//    }
//
//    @GetMapping("/api/purchaseOrdersDTO-paged")
////    @RequestMapping("/api")
//    public Flux<PurchaseOrderDTO> getQuoteFlux(final @RequestParam(name = "page") int page,
//                                    final @RequestParam(name = "size") int size) {
//        return nimbiComprasService.findAllPurchaseOrdersDTO(PageRequest.of(page, size)).delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
//    }
//
//    @ApiOperation(value = "Search Purchase Orders")
//    @PostMapping(value = "/api/purchaseOrders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
////    @RequestMapping("/api")
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = ""),
//            @ApiResponse(code = 404, message = ""),
//            @ApiResponse(code = 422, message = "Unable to find"),
//            @ApiResponse(code = 400, message = "Unable to find Purchase Orders")
//    })
//    public ResponseEntity<ResponsePurchaseOrders> searchPurchaseOrders(@RequestBody PurchaseOrdersRequestDTO purchaseOrdersRequestDTO) {
//        return ResponseEntity.ok(nimbiComprasClient.findPurchaseOrders(purchaseOrdersRequestDTO).block());
//    }
}
