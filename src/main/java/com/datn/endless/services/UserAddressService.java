package com.datn.endless.services;

import com.datn.endless.dtos.DistrictDTO;
import com.datn.endless.dtos.ProvinceDTO;
import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.dtos.WardDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserAddressException;
import com.datn.endless.models.CurrentUserAddressModel;
import com.datn.endless.models.UserAddressModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAddressService {

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    UserLoginInfomation userLoginInfomation;

    // Chuyển đổi Useraddress thành UseraddressDTO
    private UseraddressDTO convertToDTO(Useraddress address) {
        UseraddressDTO dto = new UseraddressDTO();
        dto.setAddressID(address.getAddressID());
        dto.setUserID(address.getUserID().getUserID());
        dto.setDistrictCode(address.getDistrictCode().getDistrictCode());
        dto.setProvinceCode(address.getProvinceCode().getProvinceCode());
        dto.setWardCode(address.getWardCode().getWardCode());
        dto.setWardName(address.getWardCode().getName());
        dto.setDistrictName(address.getDistrictCode().getDistrictCode());
        dto.setProvinceName(address.getProvinceCode().getProvinceCode());
        dto.setHouseNumberStreet(address.getHouseNumberStreet());
        return dto;
    }

    private List<UseraddressDTO> convertToDTOList(List<Useraddress> userAddresses) {
        return userAddresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy tất cả địa chỉ của người dùng theo userId
    public List<UseraddressDTO> getUserAddressesByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Useraddress> userAddresses = userAddressRepository.findByUser(user);
        return convertToDTOList(userAddresses);
    }

    // Lưu địa chỉ người dùng mới
    public UseraddressDTO addUserAddress(UserAddressModel userAddressModel) {
        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Ward ward = wardRepository.findById(userAddressModel.getWardCode())
                .orElseThrow(() -> new IllegalArgumentException("Ward not found"));

        Useraddress userAddress = new Useraddress();
        userAddress.setAddressID(UUID.randomUUID().toString());
        userAddress.setUserID(user);
        userAddress.setProvinceCode(ward.getDistrictCode().getProvinceCode());
        userAddress.setDistrictCode(ward.getDistrictCode());
        userAddress.setWardCode(ward);
        userAddress.setHouseNumberStreet(userAddressModel.getHouseNumberStreet());

        Useraddress savedUserAddress = userAddressRepository.save(userAddress);
        return convertToDTO(savedUserAddress);
    }

    public boolean isAddressExists(CurrentUserAddressModel currentUserAddressModel) {
        String wardCode = currentUserAddressModel.getWardCode();
        String houseNumberStreet = currentUserAddressModel.getHouseNumberStreet();
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());

        // Kiểm tra địa chỉ có trùng không dựa trên thông tin người dùng, mã phường, và số nhà
        Optional<Useraddress> existingAddress = userAddressRepository.findByUserIDAndWardCodeAndHouseNumberStreet(
                user.getUserID(), wardCode, houseNumberStreet
        );

        return existingAddress.isPresent();
    }

    public UseraddressDTO addCurrentUserAddress(CurrentUserAddressModel currentUserAddressModel) {
        try {
            String wardCode = currentUserAddressModel.getWardCode();
            String houseNumberStreet = currentUserAddressModel.getHouseNumberStreet();
            User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());

            Ward ward = wardRepository.findWardByWardCode(wardCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Ward not found"));

            Province province = provinceRepository.findProvinceByProvinceCode(ward.getDistrictCode().getProvinceCode().getCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Province not found"));

            District district = districtRepository.findDistrictByDistrictCode(ward.getDistrictCode().getCode())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found"));

            Useraddress userAddress = new Useraddress();
            userAddress.setAddressID(UUID.randomUUID().toString());
            userAddress.setUserID(user);
            userAddress.setProvinceCode(province);
            userAddress.setDistrictCode(district);
            userAddress.setWardCode(ward);
            userAddress.setHouseNumberStreet(houseNumberStreet);

            Useraddress savedUserAddress = userAddressRepository.save(userAddress);
            return convertToDTO(savedUserAddress);

        } catch (ResourceNotFoundException e) {
            // Trả về thông báo lỗi rõ ràng cho phía client
            throw new UserAddressException("Thông tin địa chỉ không đầy đủ hoặc không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            // Trả về thông báo lỗi cho bất kỳ lỗi nào khác
            throw new UserAddressException("Đã xảy ra lỗi không mong muốn. Vui lòng thử lại.");
        }
    }


    // Cập nhật địa chỉ người dùng hiện tại
    public UseraddressDTO updateUserAddress(String addressId, UserAddressModel userAddressModel) {
        Useraddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ward ward = wardRepository.findById(userAddressModel.getWardCode())
                .orElseThrow(() -> new IllegalArgumentException("Ward not found"));

        existingAddress.setUserID(user);
        existingAddress.setProvinceCode(ward.getDistrictCode().getProvinceCode());
        existingAddress.setDistrictCode(ward.getDistrictCode());
        existingAddress.setWardCode(ward);
        existingAddress.setHouseNumberStreet(userAddressModel.getHouseNumberStreet());

        Useraddress updatedUserAddress = userAddressRepository.save(existingAddress);
        return convertToDTO(updatedUserAddress);
    }

    // Xóa địa chỉ người dùng
    public void deleteUserAddress(String addressId) {
        if (!userAddressRepository.existsById(addressId)) {
            throw new IllegalArgumentException("Address not found");
        }
        userAddressRepository.deleteById(addressId);
    }

    // Lấy tất cả các tỉnh
    public List<ProvinceDTO> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(this::convertToProvinceDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi Province thành ProvinceDTO
    private ProvinceDTO convertToProvinceDTO(Province province) {
        ProvinceDTO dto = new ProvinceDTO();
        dto.setCode(province.getCode());
        dto.setName(province.getName());
        dto.setNameEn(province.getNameEn());
        dto.setFullName(province.getFullName());
        dto.setFullNameEn(province.getFullNameEn());
        dto.setCodeName(province.getCodeName());
        dto.setAdministrativeUnitId(province.getAdministrativeUnitId());
        dto.setAdministrativeRegionId(province.getAdministrativeRegionId());
        return dto;
    }

    // Lấy các quận theo tỉnh
    public List<DistrictDTO> getDistrictsByProvince(String provinceCode) {
        return districtRepository.findDistrictByProvinceCode(provinceCode).stream()
                .map(this::convertToDistrictDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi District thành DistrictDTO
    private DistrictDTO convertToDistrictDTO(District district) {
        DistrictDTO dto = new DistrictDTO();
        dto.setCode(district.getCode());
        dto.setName(district.getName());
        dto.setNameEn(district.getNameEn());
        dto.setFullName(district.getFullName());
        dto.setFullNameEn(district.getFullNameEn());
        dto.setCodeName(district.getCodeName());
        dto.setProvinceCode(district.getProvinceCode().getCode());
        dto.setAdministrativeUnitId(district.getAdministrativeUnitId());
        return dto;
    }

    // Lấy các phường theo quận
    public List<WardDTO> getWardsByDistrict(String districtCode) {
        return wardRepository.findByDistrictCode_Code(districtCode).stream()
                .map(this::convertToWardDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi Ward thành WardDTO
    private WardDTO convertToWardDTO(Ward ward) {
        WardDTO dto = new WardDTO();
        dto.setCode(ward.getCode());
        dto.setName(ward.getName());
        dto.setNameEn(ward.getNameEn());
        dto.setFullName(ward.getFullName());
        dto.setFullNameEn(ward.getFullNameEn());
        dto.setCodeName(ward.getCodeName());
        dto.setDistrictCode(ward.getDistrictCode().getCode());
        dto.setAdministrativeUnitId(ward.getAdministrativeUnitId());
        return dto;
    }
}
